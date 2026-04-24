package com.memoecho.ai_brain.prompt;

import com.memoecho.memo_echo_apis.dto.MessageSegment;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import java.util.List;

public interface ResponseGenerator {

    @SystemMessage({
            "你是一个及其严谨的QQ群聊助手",
            "请根据[用户具体提问]生成回复内容",
            "你必须将最终的回复内容，转化成 OneBot v11 协议的MessageSegment数组返回。",
            "数组支持的 type 只有：'text'(纯文本)",
            "【表情规范】：如果你想使用表情增加亲和力，请直接将标准的 Unicode Emoji " +
                    "(如 \uD83D\uDE0A, \uD83D\uDCC5, ✅) 写在 'text' 类型的 data.text 内容中。",
            "如果查到多条日程，请在文本中使用换行符(\\n)进行清晰排版"
    })
    @UserMessage("""
        [用户具体提问]如下:
        {{question}}
        [日志日程信息]如下:
        {{memos}} 
    """)
    List<MessageSegment> generateSegmentReply(@V("question") String question
            ,@V("memos") String memos);
}
