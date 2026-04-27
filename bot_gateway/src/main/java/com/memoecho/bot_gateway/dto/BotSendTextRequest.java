package com.memoecho.bot_gateway.dto;

import lombok.Data;

@Data
public class BotSendTextRequest {
    private Long groupId;
    private Long privateId;
    private String content;
}
