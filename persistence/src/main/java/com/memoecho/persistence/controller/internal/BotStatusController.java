package com.memoecho.persistence.controller.internal;

import com.memoecho.memo_echo_apis.dto.BotStatus;
import com.memoecho.persistence.service.Impl.QQBotStatusServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 🛰️ QQ机器人状况控制
 * <hr/>
 * 🧩 职责： 持久层处理QQ机器人的状况
 * 🛡️ 关联：
 * 🗺️ 架构： (MemoEcho)
 *
 * @author Sisfuzues
 * @date 2026/4/20 14:34
 */
@Slf4j(topic = "机器人情况持久层")
@RestController
@RequestMapping("/internal")
@EnableFeignClients()
@RequiredArgsConstructor
public class BotStatusController {
    private final QQBotStatusServiceImpl qqBotStatusService;

    @PostMapping("/qqbot/status")
    public Boolean setQQBotStatus(@RequestBody BotStatus botStatus){
        String botName = botStatus.getBotName();
        int status = botStatus.getStatus();
        return qqBotStatusService.setBotStatus(botName, status);
    }
}
