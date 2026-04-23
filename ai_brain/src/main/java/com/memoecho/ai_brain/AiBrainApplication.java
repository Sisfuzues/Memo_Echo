package com.memoecho.ai_brain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {"com.memoecho.memo_echo_apis.client"})
public class AiBrainApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiBrainApplication.class, args);
        System.out.println("================");
        System.out.println("    正在测试     ");
        System.out.println("================");
    }

}
