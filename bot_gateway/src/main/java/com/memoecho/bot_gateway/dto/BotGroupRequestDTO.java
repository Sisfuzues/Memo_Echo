package com.memoecho.bot_gateway.dto;

import lombok.Data;

@Data
public class BotGroupRequestDTO {
    private Long groupId;
    private String flag;
    private String subType;
    private Boolean approve;
    private String reason;
}
