package com.memoecho.bot_gateway.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.memoecho.bot_gateway.client.NapcatClient;
import com.memoecho.bot_gateway.service.BotMessageService;
import com.memoecho.bot_gateway.service.MQService;
import com.memoecho.memo_echo_apis.client.PersistenceStatusClient;
import com.memoecho.memo_echo_apis.dto.BotStatus;
import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j(topic = "QQ机器人服务")
@Service
@RequiredArgsConstructor
public class QQBotMessageServiceImpl implements BotMessageService {
    private final RestTemplate restTemplate;
    private final PersistenceStatusClient persistenceStatusClient;
    private final NapcatClient napcatClient;
    private final MQService mqService;
    private final Map<String,Long> cacheHeart = new ConcurrentHashMap<>();

    @Override
    public void sendPrivateMessage(Long userId,String txt){
        Map<String,Object> payload = new HashMap<>();
        payload.put("user_id",userId);
        payload.put("message",txt);

        try{
            JSONObject response = napcatClient.sendPrivateMsg(payload);
            Long messageId = response.getLong("message_id");
            if(messageId==null){
                log.error("消息发送私人失败，未收到消息ID。");
            }
            log.info("发送给私人QQ信息成功,消息的ID：{}", messageId);
        } catch (Exception e){
            log.error("发送私人消息失败,",e);
        }
    }

    /**
     * 处理元信息
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：已经废弃，日后升级成websocket链接可以使用
     *
     * @param jsonObject 获取的原始信息
     * @author Sisfuzues
     * @date 2026/4/20 15:49
     */
    private void handleMetaMsg(JSONObject jsonObject) {
        log.info("脉搏心跳。");
        String eventType = jsonObject.getString("meta_event_type");
        if(!Objects.equals(eventType, "heartbeat")){
            return ;  // 如果类型不一致，直接返回
        }

        String selfId = jsonObject.getLong("self_id").toString();
        JSONObject status = jsonObject.getJSONObject("status");
        Boolean statusOnline = status.getBoolean("online");
        Boolean statusGood = status.getBoolean("good");
        int res = 0;
        // 如果QQ在线，napcat良好，直接返回。
        if(statusOnline && statusGood){
            res = 1;
        }

        Long now = System.currentTimeMillis();
        Long lastTime = cacheHeart.putIfAbsent(selfId, now);
        // 拦截情况正常，并且不是第一次，且相隔时间小于15喵的信息
        if(res==1 && lastTime!=null && (now - lastTime)<15000) return;

        BotStatus botStatus = BotStatus.builder()
                .botName(selfId)
                .status(res)
                .build();

        Boolean ans = persistenceStatusClient.setQQBotStatus(botStatus);
        if(ans==null || ans.equals(Boolean.FALSE)) {
            log.warn("内部调用微服务失败。");
            return;
        }
        cacheHeart.put(selfId,now);
    }

    /**
     * 处理聊天信息
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param message 聊天信息
     * @author Sisfuzues
     * @date 2026/4/20 14:17
     */
    private void handleChatMessage(JSONObject message) {
        ReceivedMessage receivedMessage =
                JSON.parseObject(message.toString(),ReceivedMessage.class);
        String test = JSON.toJSONString(receivedMessage);
        System.out.println(test);
        Long selfId = receivedMessage.getSelfId();

        // 判断是否为at信息
        String queryAt = "[CQ:at,qq=" + selfId + "]";
        String rawMessage = receivedMessage.getRawMessage();

        // 设置消息发送的 topic tag key
        String topic = "bot_gateway-out-1";
        String tag = "group_msg_received";
        String key = receivedMessage.getMessageId().toString();  // 用messageID来存储
        if(rawMessage.contains(queryAt)){
            String messageType = receivedMessage.getMessageType();
            topic = Objects.equals(messageType, "group") ?
                    "bot_gateway-output-2":"bot_gateway-output-3";
            tag = Objects.equals(messageType,"group")?
                    "group_msg_received":"private_msg_received";
        }
        // 用mqService发送消息
        mqService.sendToMQ(receivedMessage,topic,tag,key);
    }

    @Override
    public void processBotEvent(JSONObject jsonObject, String type) {
        switch (type){
            case "message":
                handleChatMessage(jsonObject);
                break;
            case "notice":
                break;
            case "request":
                break;
            case "meta_event":
                handleMetaMsg(jsonObject);
                break;
            default:
                log.info("收到其他类型消息：{}",type);
                break;
        }
    }

    @Override
    public void sendGroupMessage(Long groupId,String txt){
        Map<String,Object> payload = new HashMap<>();
        payload.put("group_id",groupId);
        payload.put("message",txt);

        // 调用napcat提供的feign
        try{
            JSONObject response = napcatClient.sendGroupMsg(payload);
            Long messageId = response.getLong("message_id");
            if(messageId==null){
                log.error("消息响应失败，请查看。");
            }
            log.info("发送群组信息成功, messageId : {}",messageId);
        } catch (Exception e){
            log.error("发送群组失败,请查看。",e);
        }
    }
}
