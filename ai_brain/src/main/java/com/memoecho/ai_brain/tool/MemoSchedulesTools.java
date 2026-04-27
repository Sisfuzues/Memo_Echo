package com.memoecho.ai_brain.tool;

import com.memoecho.memo_echo_apis.dto.ExtractedMessage;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MemoSchedulesTools {
    /**
     * 🐊 将内容作为日程信息储存
     * <hr/>
     * 🥹 注意:
     * <pre>
     * 已经要在调用此方法的时候拼接一个系统时间，
     * 不然ai会出现幻觉。
     * </pre>
     *
     * @return java.lang.String
     * @author Sisfuzues
     * @Date 2026/4/21 18:09
     */
    @Tool("当用户输入的信息包含用户的日程信息或者集体活动,调用此方法记录。")
    String saveAsMemoSchedules(
            @P("用户日程的开始时间,要求格式：yyyy-MM-dd HH:mm:ss")
            String beginTime,
            @P("用户日程结束的时间,要求格式：yyyy-MM-dd HH:mm:ss")
            String stopTime,
            @P("相关日程要求的地点。")
            String location,
            @P("日程相关信息的简单概括。")
            String content,
            @P("日程信息的详细介绍,不能精简," +
                    "且不能出现中国政府不允许的敏感词要详细正式。")
            String introduce,
            @P("日程信息要求的参与日程的人员名单，要准确。")
            String participants
    ){
        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(beginTime,fmt);
        LocalDateTime endTime = LocalDateTime.parse(stopTime,fmt);

        ExtractedMessage.MemoScheduled build =
                ExtractedMessage.MemoScheduled.builder()
                .startTime(startTime)
                .endTime(endTime)
                .content(content)
                .introduce(introduce)
                .participants(participants)
                .location(location)
                .build();

        return "日程已经提取完毕并入库。";
    }
}
