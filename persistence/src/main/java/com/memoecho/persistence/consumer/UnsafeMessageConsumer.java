package com.memoecho.persistence.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import com.memoecho.persistence.pojo.UnsafeMessage;
import com.memoecho.persistence.service.UnsafeMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j(topic = "不安全的信息读取")
@Component("UnsafeMsgConsumer")
@RequiredArgsConstructor
public class UnsafeMessageConsumer implements Consumer<Message<UnsafeMessage>> {
    private final UnsafeMessageService unsafeMessageService;

    @Override
    public void accept(Message<UnsafeMessage> receivedMessage) {
        log.info("持久层收到消息，{}",
                receivedMessage.getPayload().toString());

        UnsafeMessage payLoad = receivedMessage.getPayload();
        UnsafeMessage unsafeMessage =
                ((JSONObject) JSON.toJSON(payLoad)).
                        toJavaObject(UnsafeMessage.class);

        boolean res = unsafeMessageService.
                updateMsgAndScore(unsafeMessage);
        if(res){
            log.info("成功统计敏感信息。");
        }else{
            log.warn("敏感信息统计失败,已经回滚,请查看日志。");
        }
    }
}