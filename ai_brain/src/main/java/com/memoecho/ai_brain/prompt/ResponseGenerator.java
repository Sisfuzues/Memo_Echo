package com.memoecho.ai_brain.prompt;

import com.memoecho.ai_brain.prompt.Entity.MessageSegment;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import java.util.List;

public interface ResponseGenerator {

    @SystemMessage
    @UserMessage
    List<MessageSegment> generateSegmentReply();
}
