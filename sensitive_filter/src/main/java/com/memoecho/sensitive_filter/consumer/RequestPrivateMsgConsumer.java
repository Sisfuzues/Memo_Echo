package com.memoecho.sensitive_filter.consumer;

import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import com.memoecho.sensitive_filter.service.MQService;
import com.memoecho.sensitive_filter.service.SensitiveWordsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j(topic = "私人信息处理类")
@Component("privateNeedsConsumer")
@RequiredArgsConstructor
public class RequestPrivateMsgConsumer implements Consumer<Message<ReceivedMessage>> {
    private final SensitiveWordsService sensitiveWordsService;
    private final MQService mqService;


    @Override
    public void accept(Message<ReceivedMessage> receivedMessage) {
        log.info("收到消息{}",receivedMessage.toString());
        ReceivedMessage payload = receivedMessage.getPayload();
        sensitiveWordsService.handleConsumerMsg("sensitive_filter-out-4", "private_msg_filtered",
                "sensitive_filter-out-2" , "group_msg_unsafe" ,payload);
    }
}
