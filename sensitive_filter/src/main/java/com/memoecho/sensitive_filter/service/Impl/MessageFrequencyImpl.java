package com.memoecho.sensitive_filter.service.Impl;

import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import com.memoecho.sensitive_filter.service.MessageFrequencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "message-frequency")
@Service
@RequiredArgsConstructor
public class MessageFrequencyImpl implements MessageFrequencyService {
    private static final int MAX_COUNT = 5;
    private static final int WINDOW_MINUTES = 5;

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean isSpam(ReceivedMessage payload) {
        Long groupId = payload.getGroupId();
        String rawMessage = payload.getRawMessage();
        if (groupId == null || rawMessage == null || rawMessage.isBlank()) {
            return false;
        }

        String redisKey = "msg_freq:" + groupId;
        String fingerprint = DigestUtils.md5DigestAsHex(
                rawMessage.trim().getBytes(StandardCharsets.UTF_8)
        );

        try {
            Double currentScore = redisTemplate.opsForZSet().incrementScore(redisKey, fingerprint, 1);
            if (currentScore != null && currentScore == 1.0d) {
                redisTemplate.expire(redisKey, WINDOW_MINUTES, TimeUnit.MINUTES);
            }

            boolean isSpam = currentScore != null && currentScore > MAX_COUNT;
            if (isSpam) {
                log.warn("[spam-drop] groupId={}, fingerprint={}, count={}", groupId, fingerprint, currentScore);
            }
            return isSpam;
        } catch (Exception e) {
            log.error("Redis frequency check failed, message will pass through", e);
            return false;
        }
    }
}
