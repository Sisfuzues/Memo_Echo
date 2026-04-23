package com.memoecho.ai_brain.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.memoecho.ai_brain.prompt.Memo;
import com.memoecho.ai_brain.prompt.ScheduleExtractor;
import com.memoecho.ai_brain.service.UnextractedMsgService;
import com.memoecho.memo_echo_apis.client.PersistenceAiDbClient;
import com.memoecho.memo_echo_apis.dto.ExtractedMessage;
import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j(topic = "ai处理过滤后的信息服务")
@Service
@RequiredArgsConstructor
public class UnextractedMsgServiceImpl implements UnextractedMsgService {
    private final EmbeddingModel embeddingModel;
    private final ChatModel chatModel;
    private final ScheduleExtractor scheduleExtractor;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final PersistenceAiDbClient aiPersistenceClient;

    @Override
    public Boolean savedMsg(ReceivedMessage msg) {
        String text = msg.getRawMessage();

        // 获取发送的准确时间
        Long sendTime = msg.getTime();
        // 将时间戳转化为 localdatetime
        Instant instant = Instant.ofEpochSecond(sendTime);
        String  now = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toString();

        // 根据发送时间估算结果
        Memo res = scheduleExtractor.extract(now, text);
        if(res==null){
            return true;
        }

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
            log.error("远程调用微服务异常，微服务名字：{},异常信息：{}"
                        ,"AiPersistenceClient",e);
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
}
