package com.memoecho.bot_gateway.service.Impl;

import com.memoecho.bot_gateway.service.MQService;
import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MQServiceImpl implements MQService {
    @Autowired
    private StreamBridge streamBridge;

    @Override
    public Boolean sendToMQ(ReceivedMessage msg, String topic, String tag, String key) {
        Message<ReceivedMessage> message = MessageBuilder.withPayload(msg)
                .setHeader("rocketmq_TAGS",tag)
                .setHeader("rocketmq_KEYS",key)
                .build();
        return streamBridge.send(topic,message);
    }
}
