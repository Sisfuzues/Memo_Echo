package com.memoecho.memo_echo_apis.client;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 🐈 持久层给ai暴露的接口
 * <hr/>
 * 🥹 注意:
 * @ClassName AiPersistenceClient
 * @Version 1.0
 * @Author Sisfuzues
 * @Date 2026/4/21 16:29
 */
@FeignClient("AiPersistenceClient")
public interface AiPersistenceClient {
   
}
