package com.memoecho.persistence.service.Impl;

import com.memoecho.persistence.service.BotStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Override
    public Map<String,Integer> getBotInfo() {
        // 获得redis里面的所有QQ机器人状态
        Set<String> keys = stringRedisTemplate.keys("bots:QQ:status:*");
        Map<String,Integer> res = new HashMap<>();
        for (var key : keys) {
            String statusStrs = stringRedisTemplate.opsForValue().get(key);
            Integer status = null;
            if (statusStrs != null) {
                status = Integer.valueOf(statusStrs);
            }
            res.put(key,status);
        }
        return res;
    }
}
