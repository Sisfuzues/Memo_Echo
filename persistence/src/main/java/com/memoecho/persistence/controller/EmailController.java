package com.memoecho.persistence.controller;

import com.memoecho.persistence.dto.EmailRequest;
import com.memoecho.persistence.dto.EmailResponse;
import com.memoecho.persistence.dto.VerifyCodeRequest;
import com.memoecho.persistence.service.EmailMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j(topic = "邮件发送")
@RestController
@RequestMapping("/email")
public class EmailController {
    @Autowired
    private EmailMessageService emailMessageService;

    @PostMapping("/send-email")
    public EmailResponse sendEmail(@RequestBody EmailRequest emailRequest){
        try{
            // 发送邮件
            return emailMessageService.sendVerificationCode(emailRequest.getEmail());
        }catch (Exception e){
            log.info("发送失败,",e);
            return new EmailResponse("500", "发送失败");

        }
    }

    @PostMapping("/verify-code")
    public EmailResponse verifyCode(@RequestBody VerifyCodeRequest verifyCodeRequest){
        try{
            // 验证码校验
            return emailMessageService.verifyCode(verifyCodeRequest.getEmail(), verifyCodeRequest.getCode());
        }catch (Exception e){
            return new EmailResponse("500", "验证失败");
        }
    }

}
