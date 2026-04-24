package com.memoecho.ai_brain.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.memoecho.ai_brain.prompt.Entity.Memo;
import com.memoecho.ai_brain.prompt.ResponseGenerator;
import com.memoecho.ai_brain.prompt.ScheduleExtractor;
import com.memoecho.ai_brain.service.UnextractedMsgService;
import com.memoecho.memo_echo_apis.client.BotControllerClient;
import com.memoecho.memo_echo_apis.client.PersistenceAiDbClient;
import com.memoecho.memo_echo_apis.dto.ExtractedMessage;
import com.memoecho.memo_echo_apis.dto.MessageSegment;
import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import com.memoecho.memo_echo_apis.dto.ResponseMessage;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j(topic = "ai处理过滤后的信息服务")
@Service
@RequiredArgsConstructor
public class UnextractedMsgServiceImpl implements UnextractedMsgService {
    private final BotControllerClient botControllerClient;
    private final EmbeddingModel embeddingModel;
    private final ChatModel chatModel;
    private final ScheduleExtractor scheduleExtractor;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final PersistenceAiDbClient aiPersistenceClient;
    private final ResponseGenerator responseGenerator;

    @Override
    public Boolean savedMsg(ReceivedMessage msg) {
        String text = msg.getRawMessage();

        // 获取发送的准确时间
        Long sendTime = msg.getTime();
        // 将时间戳转化为 localdatetime
        Instant instant = Instant.ofEpochSecond(sendTime);
        String  now = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toString();

        log.info("发送给ai进行日程提取。");
        // 根据发送时间估算结果
        Memo res = scheduleExtractor.extract(now, text);
        if(res.getContent()==null){
            log.info("未提取到日程，直接放行。");
            return true;
        }
        log.info("提取到日程。res:{}",res.toString());

        Long groupId = msg.getGroupId();
        Long senderId = msg.getSender().getUserId();
        String senderName = msg.getSender().getNickname();
        String role = msg.getSender().getRole();
        String messageId = msg.getMessageId().toString();

        // 构建发送给持久层的数据,献给MySql发数据存数据
        ExtractedMessage.MemoScheduled memo = ExtractedMessage.MemoScheduled
                .builder()
                .startTime(res.getBeginTime())
                .endTime(res.getStopTime())
                .location(res.getLocation())
                .content(res.getContent())
                .introduce(msg.getRawMessage())
                .participants(res.getParticipants())
                .build();

        ExtractedMessage ans = ExtractedMessage.builder()
                .userId(msg.getUserId())
                .originalMsgId(msg.getMessageId())
                .groupId(msg.getGroupId())
                .memo(memo)
                .build();

        String sout = JSON.toJSONString(ans, JSONWriter.Feature.PrettyFormat) ;
        log.info("发送给持久层的结果:{}",sout);
        Boolean isSaved = false;
        try{
            isSaved = aiPersistenceClient.saveMemoToDb(ans);
        }catch (Exception e){
            log.error("远程调用微服务异常，微服务名字：{},"
                        ,"AiPersistenceClient");
            log.error("具体异常信息：",e);
        }

        if(Boolean.FALSE.equals(isSaved)){
            log.error("远程存储异常。");
            return false;
        }

        Metadata metaData = new Metadata()
                .put("sender_name",senderName)
                .put("sender_id",senderId)
                .put("role",role)
                .put("group_id",groupId)
                .put("send_time",sendTime)
                .put("message_id",messageId);

        TextSegment textSegment = TextSegment.from(text, metaData);
        Embedding embedding = embeddingModel.embed(textSegment).content();
        // 再给Qdrant发数据存数据 Qdrant元信息 MessageId 用来索引 mysql 数据库
        String addRes = embeddingStore.add(embedding, textSegment);
//        log.info("向量化的内容是:{}",embedding.toString());
//
//        String sout = JSON.toJSONString(res, JSONWriter.Feature.PrettyFormat) ;
//        log.info("筛选后的日程信息:{}",sout);
        return true;
    }

    @Override
    @Async
    public void handleRequest(ReceivedMessage msg) {
        String rawMessage = msg.getRawMessage();
        Long groupId = msg.getGroupId();
        Long senderId = msg.getSender().getUserId();

        // 获得匹配了问题的向量数据
        Embedding queryEmbedding = embeddingModel.embed(rawMessage).content();
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(3)
                .minScore(0.75)
                // 筛选对应的群号
                .filter(MetadataFilterBuilder.metadataKey("group_id").isEqualTo(groupId))
                .build();

        // 去 Qdrant 查询相关数据
        EmbeddingSearchResult<TextSegment> res = embeddingStore.search(searchRequest);
        List<EmbeddingMatch<TextSegment>> matches = res.matches();

        // 查询的 MessageId
        List<Long> matchesMessageIds = new ArrayList<>();
        if(matches!=null){
            for(var match:matches){
                double score = match.score();
                TextSegment textSegment = match.embedded();

                // 拿到 Id
                String msgIdStr = textSegment.metadata().getString("message_id");
                if (msgIdStr != null) {
                    matchesMessageIds.add(Long.valueOf(msgIdStr));
                }
                log.info("提取到消息,分数：{},MessageId：{}",score,msgIdStr);
            }
        }

        List<ExtractedMessage> fromDB = new ArrayList<>();
        // 获得具体的日程信息
        if(!matchesMessageIds.isEmpty()){
            try{
                fromDB = aiPersistenceClient.getFromDB(matchesMessageIds);
            } catch (Exception e){
                log.info("远程调用读取数据库出错，错误信息:",e);
            }
        }

        StringBuilder catchMsg = new StringBuilder();
        if(fromDB!=null&&!fromDB.isEmpty()){
            int cnt = 0;
            for(var it:fromDB){
                cnt++;
                String tmp = "日程"+String.valueOf(cnt)+":";
                tmp += it.toString();
                catchMsg.append(tmp);
            }
        }else{
            List<MessageSegment> cur = new ArrayList<>();
            MessageSegment atMsg = ResponseMessage.at(senderId);
            MessageSegment textMsg = ResponseMessage
                    .text("\n抱歉，没有为您查到相关的日程安排哦。");
            cur.add(atMsg);
            cur.add(textMsg);

            ResponseMessage ans = ResponseMessage.builder()
                    .groupId(groupId).response(cur)
                    .build();
            try{
                botControllerClient.sendResponse(ans);
            } catch (Exception e){
                log.error("内部bot发送消息失败:",e);
            }
            return ;
        }

        List<MessageSegment> messageSegments = new ArrayList<>();
        String resStrs = responseGenerator
                .generateSegmentReply(rawMessage, catchMsg.toString());
        // 需要 at 提问者
        MessageSegment segment = ResponseMessage.at(senderId);
        MessageSegment ansSegment = ResponseMessage.text(resStrs);
        messageSegments.add(segment);
        messageSegments.add(ansSegment);
        log.info("ai的输出是:{}",resStrs);

        ResponseMessage ans = ResponseMessage.builder()
                .groupId(groupId)
                .response(messageSegments)
                .build();

        try{
            botControllerClient.sendResponse(ans);
        } catch (Exception e){
            log.error("内部bot发送消息失败:",e);
        }
    }
}
