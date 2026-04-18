package com.memoecho.bot_gateway;

import com.memoecho.bot_gateway.service.QQBotMessageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j(topic = "test")
class BotGatewayApplicationTests {
    @Autowired
    private QQBotMessageServiceImpl qqBotMessageService;

    @Test
    void contextLoads() {
    }

    @Test
    void readMessage(){
        log.info("开始读取信息");
    }

    @Test
    void sendMessage(){
        Long groupId = 1098307542L;
        String message = "你好你好！";

        log.info("开始发送信息。");
        qqBotMessageService.sendGroupMessage(groupId,message);
        log.info("发送完成。");
    }
}
