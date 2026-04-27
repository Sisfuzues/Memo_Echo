package com.memoecho.persistence.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "security.verification-code")
public class VerificationCodeConfig {
    
    private int codeLength = 6;
    
    private long expirationTime = 300;
    
    private long rateLimitInterval = 60;
    
    private int maxDailyCount = 5;
}
