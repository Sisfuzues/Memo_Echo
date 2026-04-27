package com.memoecho.ai_brain.consumer;

import com.memoecho.ai_brain.service.UnextractedMsgService;
import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import java.util.function.Consumer;

/**
 * 🛰️ 处理过滤后的用户请求
 * <hr/>
 * 🧩 职责： 监听消费队列，处理用户的请求，注意区分私信请求以及群组请求。
 * 🛡️ 关联： MQ规范 3-2，2-2
 * 🗺️ 架构： (MemoEcho)
 *
 * @author Sisfuzes
 * @date  2026/4/19 22:21
 */
@Component("CleanGroupRequestConsumer")
@Slf4j(topic = "筛选后的群组请求")
@RequiredArgsConstructor
public class CleanGroupRequestConsumer implements Consumer<Message<ReceivedMessage>> {
    private final UnextractedMsgService unextractedMsgService;

    @Override
    public void accept(Message<ReceivedMessage> receivedMsg) {
        log.info("获取已过滤信息，{}",
                receivedMsg.getPayload().toString());

        ReceivedMessage payload = receivedMsg.getPayload();
        unextractedMsgService.handleRequest(payload);
    }
}
