package com.memoecho.bot_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotGroupRequestResult {
    private Long groupId;
    private String status;
    private String message;
}
