package com.memoecho.persistence.controller.internal;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 🐈 储存ai处理后的信息
 * <hr/>
 * 🥹 注意:
 * @ClassName AiPersistenceController
 * @Version 1.0
 * @Author Sisfuzues
 * @Date 2026/4/21 15:45
 */
@Slf4j(topic = "AI日程持久层")
@RestController
@RequestMapping("/internal")
@EnableFeignClients()
@RequiredArgsConstructor
public class AiPersistenceController {


}
