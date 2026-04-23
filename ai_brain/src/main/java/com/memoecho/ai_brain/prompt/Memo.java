package com.memoecho.ai_brain.prompt;

import jdk.jfr.Description;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Memo{
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
    @Description("日程信息要求的参与日程的人员名单，要准确。")
    String participants;
}
