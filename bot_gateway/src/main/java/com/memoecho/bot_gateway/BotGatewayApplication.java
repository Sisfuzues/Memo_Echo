package com.memoecho.bot_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients(basePackages = {
        "com.memoecho.memo_echo_apis.client",
        "com.memoecho.bot_gateway.client"
})
@EnableScheduling
public class BotGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotGatewayApplication.class, args);
        System.out.println("================");
        System.out.println("    正在测试     ");
        System.out.println("================");
    }

}
