package com.memoecho.sensitive_filter.consumer;

import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import com.memoecho.sensitive_filter.service.MessageFrequencyService;
import com.memoecho.sensitive_filter.service.SensitiveWordsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j(topic = "group-message-filter")
@Component("botMsgConsumer")
@RequiredArgsConstructor
public class ReceivedMessageConsumer implements Consumer<Message<ReceivedMessage>> {
    private final MessageFrequencyService messageFrequencyService;
    private final SensitiveWordsService sensitiveWordsService;

    @Override
    public void accept(Message<ReceivedMessage> receivedMessage) {
        ReceivedMessage payload = receivedMessage.getPayload();
        log.info("Received group message, messageId={}", payload.getMessageId());

        if (messageFrequencyService.isSpam(payload)) {
            payload.setFilterStatus(ReceivedMessage.FilterStatus.SPAM);
            log.info("Dropped spam message, messageId={}", payload.getMessageId());
            return;
        }

        sensitiveWordsService.handleConsumerMsg(
                "sensitive_filter-out-1",
                "group_msg_filtered",
                "sensitive_filter-out-2",
                "group_msg_unsafe",
                payload
        );
    }
}
