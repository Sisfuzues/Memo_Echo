package com.memoecho.persistence.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "测试接口")
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
        System.out.println("========== 收到 GET /test/hello 请求 ==========");
        log.info("========== 收到 GET /test/hello 请求 ==========");
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello World! This is a public endpoint.");
        response.put("status", "success");
        return response;
    }

    @GetMapping("/protected")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> protectedEndpoint() {
        System.out.println("========== 收到 GET /test/protected 请求（需要 Token） ==========");
        log.info("========== 收到 GET /test/protected 请求（需要 Token） ==========");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "恭喜你！Token 验证成功！");
        response.put("status", "success");
        response.put("data", "这是受保护的接口，只有携带有效 Token 才能访问");
        return response;
    }

    @GetMapping("/user-info")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> getUserInfo() {
        System.out.println("========== 收到 GET /test/user-info 请求（需要 Token） ==========");
        log.info("========== 收到 GET /test/user-info 请求（需要 Token） ==========");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Token 验证通过！");
        response.put("status", "success");
        response.put("info", "这是一个受保护的用户信息接口");
        return response;
    }
}
