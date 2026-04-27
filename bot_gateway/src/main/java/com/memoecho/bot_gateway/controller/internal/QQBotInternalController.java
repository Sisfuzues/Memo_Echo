package com.memoecho.bot_gateway.controller.internal;

import com.memoecho.bot_gateway.service.BotMessageService;
import com.memoecho.memo_echo_apis.dto.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j(topic = "QQ机器人内部控制管理")
@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class QQBotInternalController {
    private final BotMessageService botMessageService;

    @PostMapping("/send/response")
    public void sendResponse(@RequestBody ResponseMessage responseMessage){
        botMessageService.sendGroupResponse(responseMessage);
    }
}
