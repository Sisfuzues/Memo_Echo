package com.memoecho.memo_echo_apis.vo;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnsafeMessageVO {
    private Long time;

    @JSONField(name = "post_type")
    private String postType;

    @JSONField(name = "message_type")
    private String messageType;

    @JSONField(name = "message_id")
    private Long messageId;

    @JSONField(name = "group_id")
    private Long groupId;

    @JSONField(name = "user_id")
    private Long userId;

    @JSONField(name = "raw_message")
    private String rawMessage;
    @JSONField(name = "filter_score")
    private int filterScore;

    private Long senderUserId;
    private String senderRole;
    private String senderCard;      // 群名片
    private String senderNickname;  // 全局昵称
}
