package com.memoecho.bot_gateway.task.Impl;

import com.alibaba.fastjson2.JSONObject;
import com.memoecho.bot_gateway.client.NapcatClient;
import com.memoecho.bot_gateway.task.BotStatusTask;
import com.memoecho.memo_echo_apis.client.PersistenceStatusClient;
import com.memoecho.memo_echo_apis.dto.BotStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j(topic = "QQ机器人状态监控任务")
@Component
@RequiredArgsConstructor
public class QQBotStatusTask implements BotStatusTask {
    private final NapcatClient napcatClient;
    private final PersistenceStatusClient persistenceStatusClient;

    private String selfId = null;

    @Override
    @Scheduled(fixedRate = 15000) // 十五秒监测一次
    public void checkHealth() {
        log.info("脉搏心跳。");

        int res = 0;
        try{
            // 远程调用napcat的实现类
            if(selfId == null){
                JSONObject infoJson = napcatClient.getLoginInfo();
                if(infoJson!=null && "ok".equals(infoJson.getString("status"))){
                    JSONObject data = infoJson.getJSONObject("data");
                    selfId = data.getLong("user_id").toString();
                }
            }

            JSONObject statusJson = napcatClient.getStatus();
            if(statusJson!=null && "ok".equals(statusJson.getString("status"))){
                JSONObject status = statusJson.getJSONObject("data");
                Boolean statusOnline = status.getBoolean("online");
                Boolean statusGood = status.getBoolean("good");
                if (statusOnline && statusGood) res = 1;
            }
        } catch (Exception e){
            log.error("napcat远程调用出现错误: {}",e,e.getCause());
        }

        // 如果QQ在线，napcat良好，直接返回。

        BotStatus botStatus = BotStatus.builder()
                .botName(selfId)
                .status(res)
                .build();

        try {
            Boolean ans = persistenceStatusClient.setQQBotStatus(botStatus);
            if (ans == null || ans.equals(Boolean.FALSE)) {
                log.warn("内部调用微服务失败。");
                return;
            }
        } catch (Exception e) {
           log.error("调用持久层持久化出现错误:",e);
        }
        log.info("QQ号：{},成功监测心跳。",selfId);
    }
}
