package com.memoecho.memo_echo_apis.client;


import com.memoecho.memo_echo_apis.dto.ExtractedMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "persistence",contextId = "AIDB")
public interface PersistenceAiDbClient {
    @PostMapping("/internal/persistence/memo/save")
    Boolean saveMemoToDb(@RequestBody ExtractedMessage msg);
}
