package com.memoecho.ai_brain.service.Impl;

import com.memoecho.ai_brain.service.UnextractedMsgService;
import com.memoecho.memo_echo_apis.dto.ExtractedMessage;
import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.testcontainers.shaded.org.bouncycastle.asn1.cms.MetaData;

@Slf4j(topic = "ai处理过滤后的信息服务")
@Service
@RequiredArgsConstructor
public class UnextractedMsgServiceImpl implements UnextractedMsgService {
    private final EmbeddingModel embeddingModel;
    private final ChatModel chatModel;

    @Override
    public ExtractedMessage savedMsg(ReceivedMessage msg) {
        Long sendTime = msg.getTime();
        Long groupId = msg.getGroupId();
        Long senderId = msg.getSender().getUserId();
        String senderName = msg.getSender().getNickname();
        String role = msg.getSender().getRole();

        Metadata metaData = new Metadata()
                .put("sender_name",senderName)
                .put("sender_id",senderId)
                .put("role",role)
                .put("group_id",groupId)
                .put("send_time",sendTime);

        String text = msg.getRawMessage();
        TextSegment textSegment = TextSegment.from(text,metaData);
        Embedding embedding = embeddingModel.embed(textSegment).content();

        return null;
    }
}
