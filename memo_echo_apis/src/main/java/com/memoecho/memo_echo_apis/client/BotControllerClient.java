package com.memoecho.memo_echo_apis.client;

import com.memoecho.memo_echo_apis.dto.ResponseMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "bot-gateway")
public interface BotControllerClient {
    @PostMapping("/internal/send/response")
    void sendResponse(@RequestBody ResponseMessage responseMessage);
}
