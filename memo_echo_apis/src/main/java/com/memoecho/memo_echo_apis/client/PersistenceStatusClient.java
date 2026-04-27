package com.memoecho.memo_echo_apis.client;

import com.memoecho.memo_echo_apis.dto.BotStatus;
import com.memoecho.memo_echo_apis.dto.ExtractedMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 🛰️ 持久层的BOT客户端
 * <hr/>
 * 🧩 职责：
 * 🛡️ 关联：
 * 🗺️ 架构： (MemoEcho)
 *
 * @author Sisfuzues
 * @date 2026/4/20 15:25
 */
@FeignClient(name = "persistence",contextId = "BotDB")
public interface PersistenceStatusClient {
    @PostMapping("/internal/qqbot/status")
    Boolean setQQBotStatus(@RequestBody BotStatus botStatus);

}
