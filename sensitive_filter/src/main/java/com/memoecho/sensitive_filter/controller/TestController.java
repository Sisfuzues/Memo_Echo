package com.memoecho.sensitive_filter.controller;

import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import com.memoecho.sensitive_filter.service.MQService;
import com.memoecho.sensitive_filter.service.SensitiveWordsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "测试敏感词过滤")
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@CrossOrigin
public class TestController {
    private final SensitiveWordsService sensitiveWordsService;

    @PostMapping("/sensitive_filter")
    public List<Integer> sensitiveFilter(@RequestBody List<String> msg){
        List<Integer> res = new ArrayList<>(List.of());
        for(String s:msg){
            ReceivedMessage message = new ReceivedMessage();
            message.setRawMessage(s);
            sensitiveWordsService.sensitiveWordsKill(message);
            res.add(message.getFilterScore());
        }

        return res;
    }
}
