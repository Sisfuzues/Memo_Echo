package com.memoecho.sensitive_filter.consumer;

import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import com.memoecho.sensitive_filter.service.SensitiveWordsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * 🛰️  消费请求类
 * <hr/>
 * 🧩 职责： 用于筛选用户请求，注意要判断是私信还是群聊。
 * 🛡️ 关联： MQ规范，2-1，3-1
 * 🗺️ 架构： (MemoEcho)
 *
 * @author Sisfuzues
 * @date 2026/4/19 22:19
 */
@Slf4j(topic = "群需求处理类")
@Component("groupNeedsConsumer")
@RequiredArgsConstructor
public class RequstGroupMsgConsumer implements Consumer<Message<ReceivedMessage>> {
    private final SensitiveWordsService sensitiveWordsService;

    @Override
    public void accept(Message<ReceivedMessage> receivedMessage) {
        log.info("收到消息{}",receivedMessage.toString());
        ReceivedMessage payload = receivedMessage.getPayload();
        sensitiveWordsService.handleConsumerMsg("sensitive_filter-out-3", "group_msg_filtered",
                "sensitive_filter-out-2" , "group_msg_unsafe" ,payload);
    }
}
