package com.memoecho.ai_brain.tool;

import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

@Slf4j(topic = "时间工具")
public class TimeTools {
    @Tool("获得现在的绝对时间，用来解析'今天'、'昨天'等相对时间")
    public String getCurrentDate(){
        log.info("发动时间tools");
        return LocalDateTime.now().toString();
    }
}
