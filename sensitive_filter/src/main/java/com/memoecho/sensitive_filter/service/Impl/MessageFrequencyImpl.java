package com.memoecho.sensitive_filter.service.Impl;

import com.memoecho.sensitive_filter.service.MessageFrequencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.concurrent.TimeUnit;

@Slf4j(topic = "重复刷屏消息筛查")
@Service
@RequiredArgsConstructor
public class MessageFrequencyImpl implements MessageFrequencyService {

    private final StringRedisTemplate redisTemplate;

    //阈值配置
    private static final int MAX_COUNT=5;    //最大重复次数
    private  static final int WINDOW_MINUTES=5;  //统计时间窗口

    @Override
    public boolean isSpam(Long groupId, String rawMessage) {
        if(groupId==null||rawMessage==null||rawMessage.isBlank()){
            return false;
        }
        String redisKey="msg_freq"+groupId;
        //计算MD5的消息指纹并加上trim预防空格绕过
        String fingerprint= DigestUtils.md5DigestAsHex(rawMessage.trim().getBytes());

        try{
            //增加计数
            Double currentScore=redisTemplate.opsForZSet().incrementScore(redisKey,fingerprint,1);
            //如果是第一次记录，设置过期时间
            if(currentScore!=null&&currentScore==1.0){
                redisTemplate.expire(redisKey,WINDOW_MINUTES, TimeUnit.MINUTES);
            }
            //判断是否超限
            boolean isLimit=currentScore !=null&&currentScore >MAX_COUNT;
            if(isLimit){
                log.warn("[刷屏拦截] 群：{}，消息指纹：{}，当前频率{}",groupId,fingerprint,currentScore);
            }
            return isLimit;
        }catch (Exception e){
            log.error("Redis 频率统计异常",e);
            return false;//redis挂了的话放行
        }
    }
}
