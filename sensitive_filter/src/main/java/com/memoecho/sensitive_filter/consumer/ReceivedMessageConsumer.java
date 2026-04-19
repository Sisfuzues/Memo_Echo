package com.memoecho.sensitive_filter.consumer;

import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import com.memoecho.sensitive_filter.service.MQService;
import com.memoecho.sensitive_filter.service.SensitiveWordsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import java.util.function.Consumer;


@Slf4j(topic = "过滤群日程信息")
@Component("botMsgConsumer")
@RequiredArgsConstructor
public class ReceivedMessageConsumer implements Consumer<Message<ReceivedMessage>> {
    private final SensitiveWordsService sensitiveWordsService;
    private final MQService mqService;

    @Override
    public void accept(Message<ReceivedMessage> receivedMessage) {
        log.info("收到消息{}",receivedMessage.toString());
        ReceivedMessage payload = receivedMessage.getPayload();
        String rawMessage = payload.getRawMessage();

        // 是否被斩杀
        String topic,tag,key;
        key = payload.getMessageId().toString();
        if(sensitiveWordsService.sensitiveWordsKill(payload)){
            payload.setFilterStatus(ReceivedMessage.FilterStatus.UNSAFE);
            topic = "topic_security_alerts";
            tag = "group_msg_unsafe";
            mqService.sendToMQ(payload,topic,tag,key);
        }else{
            payload.setFilterStatus(ReceivedMessage.FilterStatus.SAFE);
        }

        // 不管有没有筛选过，都发送给AI
        topic = "sensitive_filter-out-1";
        tag = "group_msg_filtered";
        mqService.sendToMQ(payload,topic,tag,key);
    }
}
