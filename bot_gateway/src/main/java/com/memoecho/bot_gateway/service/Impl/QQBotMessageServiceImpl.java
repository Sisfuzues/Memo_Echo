package com.memoecho.bot_gateway.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.memoecho.bot_gateway.service.QQBotMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "QQBotMessage")
@Service
public class QQBotMessageServiceImpl implements QQBotMessageService {
    private final RestTemplate restTemplate;

    private final String API_BASE = "http://localhost:3011";

    public QQBotMessageServiceImpl(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    /**
     *  QQbot发送私人信息
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param userId 用户的ID, txt 字符串类型的消息数据
     * @author Sisfuzues
     * @date 2026/4/13 11:11
     */
    @Override
    public void sendPrivateMessage(Long userId,String txt){
        String url  = API_BASE + "/send_private_msg";

        Map<String,Object> payloud = new HashMap<>();
        payloud.put("group_id",userId);
        payloud.put("message",txt);

        String jsonMessage = JSON.toJSONString(payloud);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization","Bearer 1TTQ51pa-Q_Hr7kl");

        HttpEntity<String> request = new HttpEntity<>(jsonMessage,headers);
        try{
            String response = restTemplate.postForObject(url,request,String.class);
            log.info("发送信息成功,返回值："+response);
        } catch (Exception e){
            log.error("发送失败,",e);
        }
    }

    /**
     *  QQbot发送群体信息
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param groupId 用户的ID, txt 字符串类型的消息数据
     * @author Sisfuzues
     * @date 2026/4/13 11:11
     */
    @Override
    public void sendGroupMessage(Long groupId,String txt){
        String url  = API_BASE + "/send_group_msg";

        Map<String,Object> payloud = new HashMap<>();
        payloud.put("group_id",groupId);
        payloud.put("message",txt);

        String jsonMessage = JSON.toJSONString(payloud);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization","Bearer 1TTQ51pa-Q_Hr7kl");

        HttpEntity<String> request = new HttpEntity<>(jsonMessage,headers);
        try{
            String response = restTemplate.postForObject(url,request,String.class);
            log.info("发送信息成功,返回值："+response);
        } catch (Exception e){
            log.error("发送失败,",e);
        }
    }
}
