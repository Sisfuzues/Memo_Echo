package com.memoecho.memo_echo_apis.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedMessage implements Serializable {
    private Long time;
    @JSONField(name = "self_id")
    private Long selfId;

    @JSONField(name = "post_type")
    private String postType;

    private String flag;

    @JSONField(name = "message_type")
    private String messageType;

    @JSONField(name = "sub_type")
    private String subType;

    @JSONField(name = "message_id")
    private Long messageId;

    @JSONField(name = "group_id")
    private Long groupId;

    @JSONField(name = "user_id")
    private Long userId;

    @JSONField(name = "raw_message")
    private String rawMessage;
    private Sender sender;
    @JSONField(name = "filter_score")
    private int filterScore;

    @Builder.Default
    private FilterStatus filterStatus = FilterStatus.UNFILTER; // 这个用来判断有没有经历过筛选

    public enum FilterStatus{
        UNFILTER,  // 初始的未过滤的信息
        SAFE,      // 已经过滤的安全信息
        UNSAFE     // 过滤后的不安全信息
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Sender implements Serializable{
        @JSONField(name = "user_id")
        private Long userId;
        private String role;
        private String card;      // 群名片
        private String nickname;  // 全局昵称
    }
}

