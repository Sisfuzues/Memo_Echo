package com.memoecho.memo_echo_apis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BotStatus {
    private String botName;
    private int status;  // 1 为在线，0为异常
}
