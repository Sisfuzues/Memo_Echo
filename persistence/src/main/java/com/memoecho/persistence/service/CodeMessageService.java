package com.memoecho.persistence.service;

import com.memoecho.persistence.dto.EmailResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface CodeMessageService {


    //将验证码存储到缓存中
    public void saveCode(String email, String code);

    // 生成验证码
    public String generateVerificationCode();

    // 验证码校验
    public EmailResponse verifyCode(String email, String code);


}
