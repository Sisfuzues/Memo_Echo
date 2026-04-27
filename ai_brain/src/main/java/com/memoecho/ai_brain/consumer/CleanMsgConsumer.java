package com.memoecho.ai_brain.consumer;

import com.memoecho.ai_brain.service.MQService;
import com.memoecho.ai_brain.service.UnextractedMsgService;
import com.memoecho.memo_echo_apis.dto.ExtractedMessage;
import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import java.util.function.Consumer;

/**
 * 🛰️ 消费过滤层信息类
 * <hr/>
 * 🧩 职责： MQ 1.3
 * 🛡️ 关联：
 * 🗺️ 架构： (MemoEcho)
 *
 * @author Sisfuzues
 * @date 2026/4/19 22:14
 */
@Component("CleanMsgConsumer")
@Slf4j(topic = "筛选后信息消费类")
@RequiredArgsConstructor
public class CleanMsgConsumer implements Consumer<Message<ReceivedMessage>> {
    private final UnextractedMsgService unextractedMsgService;

    @Override
    public void accept(Message<ReceivedMessage> receivedMsg) {
        log.info("获取已过滤信息，{}",
                receivedMsg.getPayload().toString());

        ReceivedMessage payload = receivedMsg.getPayload();
        Boolean res = unextractedMsgService.savedMsg(payload);

        if(Boolean.FALSE.equals(res)){
            log.error("消息未能成功存储日程，消息 MessageId:{}"
                    ,payload.getMessageId());
        }
        log.info("成功存储日程。");
    }
}