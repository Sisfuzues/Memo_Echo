package com.memoecho.ai_brain.prompt;

import com.alibaba.fastjson2.annotation.JSONField;
import com.memoecho.memo_echo_apis.dto.ExtractedMessage;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import jdk.jfr.Description;
import org.springframework.cglib.core.Local;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface ScheduleExtractor {

    /**
     * 🐊 解析用户日程
     * <hr/>
     * 🥹 注意:
     * <pre>
     * 如果用户输入不是日程信息，则过滤后的信息会把所有字段制成null
     * </pre>
     * @param currentTime 当前时间
     * @param userInput 用户的输入也就是聊天信息
     * @return MemoScheduled
     * @author Sisfuzues
     * @Date 2026/4/21 19:19
     */
    @SystemMessage({
            "你是一个及其严格的用户日程提取器。",
            "当前服务器的绝对时间是: {{currentTime}}。",
            "请通过当前时间推断出用户发送的信息日程绝对时间(例如'明天')。",
            "如果用户输入的信息与日程无关例如（’我不太想玩游戏‘）,",
            "请将所有字段返回 null,",
            "不能捏造数据。"
    })
    @UserMessage("请从下面输入的文本中提取结构化的日程信息:\n\n {{userInput}}")
    ExtractedMessage.MemoScheduled  extract(
        @V("currentTime") String currentTime,
        @V("userInput") String userInput
    );
}

class MemoScheduled{
    @Description("用户日程开始时间，" +
            "要求格式：yyyy-MM-dd HH:mm:ss,必须是标准 ISO 格式")
    LocalDateTime beginTime;
    @Description("用户日程结束的时间," +
            "要求格式：yyyy-MM-dd HH:mm:ss,必须是标准 ISO 格式")
    LocalDateTime stopTime;

    @Description("相关日程要求的地点,或者网络平台。")
    String location;
    @Description("日程相关信息的简单概括。")
    String content;
    @Description("日程信息的详细介绍,不能精简," +
            "且不能出现中国政府不允许的敏感词要详细正式。")
    String introduce;
    @Description("日程信息要求的参与日程的人员名单，要准确。")
    String participants;
}
