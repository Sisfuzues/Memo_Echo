package com.memoecho.ai_brain.prompt;

import com.memoecho.memo_echo_apis.dto.MessageSegment;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import java.util.List;

public interface ResponseGenerator {

    @SystemMessage({
            "你是一个极其严谨且友好的QQ群聊助手。",
            "当你遇到相对时间时，请务必先获取当前日期进行计算",
            "你的任务是：根据提供的[日志日程信息]，回答[用户具体提问]。",
            "### 回复规范 ###",
            "1. 直接输出人类可读的自然语言，严禁输出任何 JSON、代码块或 MarkDown格式。",
            "2. 使用 Unicode Emoji 增加亲和力（例如：📅 表示日期, ✅ 表示完成, 💡 表示提醒）。",
            "3. 如果找到多条日程，请按时间顺序排列，并使用换行符(\\n)进行清晰的分点罗列。",
            "4. 如果[日志日程信息]中没有查到相关内容，请礼貌地告知用户，并鼓励他通过你记录新的日程。",
            "如果查到多条日程，请在文本中使用换行符(\\n)进行清晰排版"
    })
    @UserMessage("""
        [用户具体提问]如下:
        {{question}}
        [日志日程信息]如下:
        {{memos}} 
    """)
    String generateSegmentReply(@V("question") String question
            ,@V("memos") String memos);
}
