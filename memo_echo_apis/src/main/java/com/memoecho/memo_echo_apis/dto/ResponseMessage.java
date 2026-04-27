package com.memoecho.memo_echo_apis.dto;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ResponseMessage {
    Long groupId;
    Long privateId;
    List<MessageSegment> response;

    public static MessageSegment at(Long senderId){
        MessageSegment atSeg = new MessageSegment();
        atSeg.setType("at");
        Map<String, Object> atData = new HashMap<>();
        atData.put("qq", String.valueOf(senderId));
        atSeg.setData(atData);
        return atSeg;
    }

    public static MessageSegment text(String content){
        MessageSegment textSeg = new MessageSegment();
        textSeg.setType("text");
        Map<String, Object> textData = new HashMap<>();
        textData.put("text", content);
        textSeg.setData(textData);
        return textSeg;
    }
}

