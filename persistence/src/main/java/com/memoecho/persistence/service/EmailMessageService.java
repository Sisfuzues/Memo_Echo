package com.memoecho.persistence.service;

import com.memoecho.persistence.dto.EmailResponse;

public interface EmailMessageService {

    // 发送邮件
    public EmailResponse sendVerificationCode(String email);

    // 创建邮件内容
    public String createVerifyCodePage(String Code);

    // 验证邮箱验证码
    public EmailResponse verifyCode(String email, String code);
}
