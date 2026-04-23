package com.memoecho.persistence.controller.internal;


import com.memoecho.memo_echo_apis.dto.ExtractedMessage;
import com.memoecho.persistence.pojo.Memo;
import com.memoecho.persistence.service.Impl.MemoServiceImpl;
import com.memoecho.persistence.service.MemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@EnableDiscoveryClient
@EnableFeignClients()
@RequiredArgsConstructor
public class AiPersistenceController {
    private final MemoServiceImpl memoService;

    @PostMapping("/persistence/memo/save")
    public Boolean saveMemoToDb(@RequestBody ExtractedMessage msg){
        Memo memo = Memo.builder()
                .messageId(msg.getOriginalMsgId())
                .senderId(msg.getUserId())
                .groupId(msg.getGroupId())
                .startTime(msg.getMemo().getStartTime())
                .endTime(msg.getMemo().getEndTime())
                .location(msg.getMemo().getLocation())
                .content(msg.getMemo().getContent())
                .introduce(msg.getMemo().getIntroduce())
                .participants(msg.getMemo().getParticipants())
                .build();
        return memoService.save(memo);
    }

}
