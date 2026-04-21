package com.memoecho.persistence.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface CodeMessageService {


    //将验证码存储到缓存中
    public void saveCode(String email, String code);

    // 生成验证码
    public String generateVerificationCode();

    // 验证码校验
    public boolean verifyCode(String email, String code);


}
