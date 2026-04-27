package com.memoecho.ai_brain.prompt;

import com.memoecho.ai_brain.prompt.Entity.Memo;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

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
    Memo extract(
        @V("currentTime") String currentTime,
        @V("userInput") String userInput
    );
}

