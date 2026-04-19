package com.memoecho.memo_echo_apis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyMessage implements Serializable {
    private String type;
    private Long groupId;
    private Long userId;

    private String replyText;    // 回复的最终信息

    private Long originalMsgId;  // 溯源Id
}
