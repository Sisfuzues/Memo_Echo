package com.memoecho.bot_gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.memoecho.bot_gateway.service.QQBotMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "BotReader")
@RestController
@RequestMapping("/api/bot")
public class QQBotController {
    @Autowired
    private QQBotMessageService qqBotMessageService;

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
     * @date 2026/4/13 14:15
     */
    @PostMapping("/webhook")
    public String readMessage(@RequestBody JSONObject message){
        log.info("收到信息：{}",message.toJSONString());
        String type = message.getString("post_type");

        if("message".equals(type)){
            String messageType = message.getString("message_type");

            if("group".equals(messageType)){
                Long groupId = message.getLong("group_id");
                String txt = message.getString("raw_message");
                qqBotMessageService.sendGroupMessage(groupId,txt);
            }
        }

        return "success";
    }
}
