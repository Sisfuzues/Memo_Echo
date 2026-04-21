package com.memoecho.ai_brain;

import com.alibaba.fastjson2.JSON;
import com.memoecho.ai_brain.service.Impl.UnextractedMsgServiceImpl;
import com.memoecho.ai_brain.service.MQService;
import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.springframework.util.ResourceUtils;


@SpringBootTest
@Slf4j(topic = "测试ai对话模型")
class AiBrainApplicationTests {
    @Autowired
    private UnextractedMsgServiceImpl unextractedMsgService;

    @Test
    void testAiService() throws IOException {
        log.info("================== 开始测试 ====================");
        File file =  ResourceUtils.getFile("classpath:documents/data.txt");

        String text = Files.readString(file.toPath());
        log.info("原始字符串:{}",text);
        ReceivedMessage receivedMessage = JSON.parseObject(text,ReceivedMessage.class);
        log.debug("读取的结果是:{}",receivedMessage.toString());

        unextractedMsgService.savedMsg(receivedMessage);
        log.info("================== 测试结束 ====================");
    }

    @Test
    void contextLoads() {
    }

}
