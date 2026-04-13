package com.memoecho.bot_gateway;

import com.memoecho.bot_gateway.service.QQBotMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j(topic = "test")
class BotGatewayApplicationTests {
    @Autowired
    private QQBotMessage qqBotMessage;

    @Test
    void contextLoads() {
    }


    @Test
    void sendMessage(){
        Long groupId = 1098307542L;
        String message = "我是java后端程序";

        log.info("开始发送信息。");
        qqBotMessage.sendGroupMessage(groupId,message);
        log.info("发送完成。");
    }
}
