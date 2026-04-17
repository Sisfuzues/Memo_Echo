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
public class ReceivedMessage implements Serializable {
    private Long time;
    private Long selfId;
    private String postType;
    private String flag;
    private String messageType;
    private String subType;
    private Long messageId;
    private Long groupId;
    private Long userId;
    private String rawMessage;
    private Sender sender;

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
        private Long userId;
        private String role;
    }
}

