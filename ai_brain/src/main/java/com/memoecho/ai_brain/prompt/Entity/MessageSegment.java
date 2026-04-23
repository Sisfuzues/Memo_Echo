package com.memoecho.ai_brain.prompt.Entity;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
public class MessageSegment {
    private String type;  // 类型 "text", "at", "image" 等
    private Map<String, Object> data; // 具体信息，例如 {"text": "你好"} 或 {"qq": "123456"}
}
