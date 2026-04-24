package com.memoecho.bot_gateway.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.memoecho.bot_gateway.service.Impl.QQBotMessageServiceImpl;
import com.memoecho.memo_echo_apis.dto.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 🛰️ 处理 bot 的各种任务
 * <hr/>
 * 🧩 职责：
 * 🛡️ 关联：
 * 🗺️ 架构： (MemoEcho)
 *
 * @author Sisfuzues
 * @date  2026/4/19 22:50
 */
@Slf4j(topic = "BotReader")
@RestController
@RequestMapping("/api/bot")
@RequiredArgsConstructor
public class QQBotController {
    private final QQBotMessageServiceImpl botMessageService;

    /**
     *  获取机器人的所有信息
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
        log.debug("收到原始信息：{}",message);

        JSONObject jsonObject = JSON.parseObject(message);
        String type = jsonObject.getString("post_type");

        if(type == null){
            log.warn("获取到未知类型消息。消息内容:{}",message);
            return "ignore";
        }

        botMessageService.processBotEvent(jsonObject,type);
        return "{}";
    }

    @PostMapping("/send/response")
    public void sendResponse(@RequestBody ResponseMessage responseMessage){
        botMessageService.sendGroupResponse(responseMessage);
    }
}
