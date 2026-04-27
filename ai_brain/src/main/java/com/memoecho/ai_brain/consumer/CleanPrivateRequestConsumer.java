package com.memoecho.ai_brain.consumer;

import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;


@Component("CleanPrivateRequestConsumer")
@Slf4j(topic = "筛选后的个人请求")
@RequiredArgsConstructor
public class CleanPrivateRequestConsumer implements Consumer<Message<ReceivedMessage>> {
    @Override
    public void accept(Message<ReceivedMessage> receivedMessage) {

    }
}
