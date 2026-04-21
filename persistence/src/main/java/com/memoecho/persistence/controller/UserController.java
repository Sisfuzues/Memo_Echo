package com.memoecho.persistence.controller;

import com.memoecho.persistence.dto.LoginRequest;
import com.memoecho.persistence.dto.LoginResponse;
import com.memoecho.persistence.dto.RegisterRequest;
import com.memoecho.persistence.dto.RegisterResponse;
import com.memoecho.persistence.pojo.User;
import com.memoecho.persistence.service.UserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 🛰️ 处理用户登录注册,管理员帐号申请。
 * <hr/>
 * 🧩 职责：
 * 🛡️ 关联：
 * 🗺️ 架构： (MemoEcho)
 *
 * @author Nicky
 * &#064;date  2026/4/19 22:29
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMessageService userMessageService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest){
        return userMessageService.UserLogin(loginRequest);
    }
    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest){
        return userMessageService.UserRegister(registerRequest);
    }
}

