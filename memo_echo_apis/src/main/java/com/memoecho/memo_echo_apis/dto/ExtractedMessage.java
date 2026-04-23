package com.memoecho.memo_echo_apis.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExtractedMessage implements Serializable {
    private Long originalMsgId;
    private Long userId;
    private Long groupId;
    private MemoScheduled memo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemoScheduled implements Serializable {
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startTime;

        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endTime;

        private String location;       // 地点
        private String content;        // 日程简介
        private String introduce;      // 日程详情
        private String participants;   // 参与同学
    }
}