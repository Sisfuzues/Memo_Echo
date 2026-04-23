package com.memoecho.persistence.service.Impl;


import com.memoecho.persistence.config.VerificationCodeConfig;
import com.memoecho.persistence.dto.EmailResponse;
import com.memoecho.persistence.service.CodeMessageService;
import com.memoecho.persistence.util.AesUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "验证码服务")
public class CodeMessageServiceImpl implements CodeMessageService {
    
    private static final String VERIFY_CODE_PREFIX = "verify_code:";
    private static final String RATE_LIMIT_PREFIX = "rate_limit:";
    private static final String DAILY_COUNT_PREFIX = "daily_count:";
    
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final StringRedisTemplate redisTemplate;
    private final AesUtil aesUtil;
    private final VerificationCodeConfig codeConfig;

    @Override
    public void saveCode(String email, String code) {
        String key = VERIFY_CODE_PREFIX + email;
        String encryptedCode = aesUtil.encrypt(code);
        redisTemplate.opsForValue().set(key, encryptedCode, codeConfig.getExpirationTime(), TimeUnit.SECONDS);
        log.info("验证码已加密存储到Redis: email={}, 有效期={}秒", email, codeConfig.getExpirationTime());
    }

    @Override
    public String generateVerificationCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < codeConfig.getCodeLength(); i++) {
            code.append(SECURE_RANDOM.nextInt(10));
        }
        return code.toString();
    }

    @Override
    public EmailResponse verifyCode(String email, String code) {
        String key = VERIFY_CODE_PREFIX + email;
        String encryptedCode = redisTemplate.opsForValue().get(key);
        
        if (encryptedCode == null) {
            log.warn("验证码不存在或已过期: email={}", email);
            return new EmailResponse("500", "验证码已过期或不存在");
        }
        
        try {
            String decryptedCode = aesUtil.decrypt(encryptedCode);
            if (!decryptedCode.equals(code)) {
                log.warn("验证码错误: email={}", email);
                return new EmailResponse("500", "验证码错误");
            }

            return new EmailResponse("200", "验证成功");
        } catch (Exception e) {
            log.error("验证码解密失败: email={}", email, e);
            return new EmailResponse("500", "验证失败");
        }
    }

    @Override
    public boolean deleteCode(String email){
        String key = VERIFY_CODE_PREFIX + email;
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    @Override
    public boolean checkRateLimit(String email) {
        String rateLimitKey = RATE_LIMIT_PREFIX + email;
        Boolean exists = redisTemplate.hasKey(rateLimitKey);
        if (Boolean.TRUE.equals(exists)) {
            log.warn("验证码发送过于频繁: email={}, 请{}秒后再试", email, codeConfig.getRateLimitInterval());
            return false;
        }
        redisTemplate.opsForValue().set(rateLimitKey, "1", codeConfig.getRateLimitInterval(), TimeUnit.SECONDS);
        return true;
    }

    @Override
    
    public boolean checkDailyLimit(String email) {
        String today = java.time.LocalDate.now().toString();
        String dailyCountKey = DAILY_COUNT_PREFIX + email + ":" + today;
        String countStr = redisTemplate.opsForValue().get(dailyCountKey);
        
        int count = 0;
        if (countStr != null) {
            count = Integer.parseInt(countStr);
        }
        
        if (count >= codeConfig.getMaxDailyCount()) {
            log.warn("今日验证码发送次数已达上限: email={}, count={}/{}", email, count, codeConfig.getMaxDailyCount());
            return false;
        }
        
        redisTemplate.opsForValue().increment(dailyCountKey);
        redisTemplate.expire(dailyCountKey, 24, TimeUnit.HOURS);
        return true;
    }
}
