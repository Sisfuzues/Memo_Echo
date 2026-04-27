package com.memoecho.persistence.service.Impl;

import com.memoecho.persistence.config.MailConfig;
import jakarta.mail.internet.MimeMessage;
import com.memoecho.persistence.dto.EmailResponse;
import com.memoecho.persistence.service.CodeMessageService;
import com.memoecho.persistence.service.EmailMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "邮件发送服务")
public class EmailMessageServiceImpl implements EmailMessageService {

    private final JavaMailSender mailSender;

    @Autowired
    private MailConfig mailConfig;

    @Autowired
    private CodeMessageService codeMessageService;

    @Override
    public EmailResponse sendVerificationCode(String email) {
        if (!((CodeMessageServiceImpl) codeMessageService).checkRateLimit(email)) {
            return new EmailResponse("500", "请勿频繁请求，请60秒后再试");
        }
        
        if (!((CodeMessageServiceImpl) codeMessageService).checkDailyLimit(email)) {
            return new EmailResponse("500", "今日发送次数已达上限（5次）");
        }
        
        String code = codeMessageService.generateVerificationCode();

        codeMessageService.saveCode(email, code);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailConfig.getUsername());
            helper.setTo(email);
            helper.setSubject("MemoEcho验证码");
            String content = createVerifyCodePage(code);
            helper.setText(content, true);
            mailSender.send(message);

            log.info("验证码发送成功: email={}", email);
            return new EmailResponse("200", "发送成功");

        } catch (Exception e) {
            log.error("验证码发送失败: email={}", email, e);
            return new EmailResponse("500", "发送失败");
        }
    }



    @Override
    public String createVerifyCodePage(String code) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f5f5f5; margin: 0; padding: 20px; }" +
                ".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                ".header { background-color: #4CAF50; color: white; padding: 30px; text-align: center; }" +
                ".content { padding: 40px 30px; }" +
                ".code-box { background-color: #f0f0f0; border-left: 4px solid #4CAF50; padding: 20px; margin: 20px 0; text-align: center; }" +
                ".code { font-size: 36px; font-weight: bold; color: #4CAF50; letter-spacing: 5px; }" +
                ".footer { background-color: #f9f9f9; padding: 20px; text-align: center; color: #999; font-size: 12px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "<div class=\"header\">" +
                "<h1>验证码</h1>" +
                "</div>" +
                "<div class=\"content\">" +
                "<p>您好！</p>" +
                "<p>您正在请求验证码，请使用以下验证码完成验证：</p>" +
                "<div class=\"code-box\">" +
                "<div class=\"code\">" + code + "</div>" +
                "</div>" +
                "<p style=\"color: #999; font-size: 14px;\">此验证码将在 <strong>5分钟</strong> 后过期，请勿泄露给他人。</p>" +
                "<p style=\"color: #999; font-size: 14px;\">如果这不是您的操作，请忽略此邮件。</p>" +
                "</div>" +
                "<div class=\"footer\">" +
                "<p>此邮件为系统自动发送，请勿回复</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    @Override
    public EmailResponse verifyCode(String email, String code) {
        return codeMessageService.verifyCode(email, code);
    }

}



