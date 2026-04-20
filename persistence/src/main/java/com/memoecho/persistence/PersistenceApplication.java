package com.memoecho.persistence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PersistenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersistenceApplication.class, args);
        System.out.println("================");
        System.out.println("    正在测试     ");
        System.out.println("================");
    }

}
