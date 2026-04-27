package com.memoecho.persistence.controller;

import com.memoecho.common.response.ApiResponse;
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
    public ApiResponse<EmailResponse> sendEmail(@RequestBody EmailRequest emailRequest){
        try{
            EmailResponse response = emailMessageService.sendVerificationCode(emailRequest.getEmail());
            return ApiResponse.success(response.getMessage(), response);
        }catch (Exception e){
            log.info("发送失败,",e);
            return ApiResponse.fail(500, "发送失败");
        }
    }

    @PostMapping("/verify-code")
    public ApiResponse<EmailResponse> verifyCode(@RequestBody VerifyCodeRequest verifyCodeRequest){
        try{
            EmailResponse response = emailMessageService.verifyCode(verifyCodeRequest.getEmail(), verifyCodeRequest.getCode());
            return ApiResponse.success(response.getMessage(), response);
        }catch (Exception e){
            return ApiResponse.fail(500, "验证失败");
        }
    }

}
