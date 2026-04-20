package com.memoecho.persistence.service.Impl;

import com.memoecho.persistence.service.BotStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j(topic =  "QQ机器人状态更改")
@Service
@RequiredArgsConstructor
public class QQBotStatusServiceImpl implements BotStatusService {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Boolean setBotStatus(String botName, int status) {
        String key = "bots:QQ:status:";
        try{
            stringRedisTemplate.opsForValue().
                    set(key+botName,
                            String.valueOf(status),
                            15,
                            TimeUnit.MINUTES);
            return true;
        } catch (Exception e){
            log.warn("插入机器人状态失败:",e);
            return false;
        }
    }
}
