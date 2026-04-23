package com.memoecho.persistence.controller;

import com.memoecho.persistence.dto.*;
import com.memoecho.persistence.service.UserMessageService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "用户登录注册")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMessageService userMessageService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest){
        
        log.info("请求参数: userId={}", loginRequest.getUserId());
        LoginResponse response = userMessageService.UserLogin(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<?>  register(@Valid @RequestBody RegisterRequest registerRequest){
        log.info("请求参数: userId={}, email={}", registerRequest.getUserId(), registerRequest.getEmail());
        RegisterResponse response = userMessageService.UserRegister(registerRequest);
        log.info("注册结果: {}", response.getMessage());
        return ResponseEntity.ok(response);
    }

}

