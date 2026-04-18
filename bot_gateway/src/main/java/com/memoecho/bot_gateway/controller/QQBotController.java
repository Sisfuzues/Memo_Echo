package com.memoecho.bot_gateway.controller;

import com.alibaba.fastjson2.JSON;
import com.memoecho.bot_gateway.service.MQService;
import com.memoecho.bot_gateway.service.QQBotMessageService;
import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j(topic = "BotReader")
@RestController
@RequestMapping("/api/bot")
public class QQBotController {
    @Autowired
    private QQBotMessageService qqBotMessageService;

    @Autowired
    private MQService mqService;

    /**
     *
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param message JSONObject格式的信息
     * @return String 类型的信息，告诉NapCat有没有接收成功
     * @author Sisfuzues
     * &#064;date  2026/4/13 14:15
     */
    @PostMapping("/webhook")
    public String readMessage(@RequestBody String message){
        log.info("收到信息：{}",message);

        ReceivedMessage receivedMessage =
                JSON.parseObject(message,ReceivedMessage.class);
        String test = JSON.toJSONString(receivedMessage);
        System.out.println(test);
        Long selfId = receivedMessage.getSelfId();

        // 判断是否为at信息
        String queryAt = "[CQ:at,qq=" + selfId + "]";
        String rawMessage = receivedMessage.getRawMessage();

        // 设置消息发送的 topic tag key
        String topic = "bot_gateway-out-1_1";
        String tag = "group_msg_received";
        String key = receivedMessage.getMessageId().toString();  // 用messageID来存储
        if(rawMessage.contains(queryAt)){
            String messageType = receivedMessage.getMessageType();
            topic = Objects.equals(messageType, "group") ?
                    "bot_gateway-output-2_1":"bot_gateway-output-3_1";
            tag = Objects.equals(messageType,"group")?
                    "group_msg_received":"private_msg_received";
        }
        // 用mqService发送消息
        mqService.sendToMQ(receivedMessage,topic,tag,key);

        return "success";
    }
}
