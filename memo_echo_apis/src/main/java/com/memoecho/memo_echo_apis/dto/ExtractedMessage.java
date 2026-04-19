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
    private List<Float> vector;
    private String chunkText;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemoScheduled implements Serializable {
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startTime;

        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endTime;

        private String location;       // 地点
        private String content;        // 日程简介
        private String introduce;      // 日程介绍
        private String participants;   // 参与同学
    }
}

//
//        "originalMsgId": -192837465,     // 溯源ID
//        "userId": 1122334455,            // 必须要有！入库MySQL和鉴权必须用
//        "groupId": 987654321,            // 群号
//
//        // 1. 结构化业务数据 (交给 MySQL)
//        "schedule": {
//        "startTime": 1712401200000,    // 建议用具体的 startTime 和 endTime，比数组更清晰
//        "endTime": 1712408400000,
//        "location": "三教",
//        "content": "开会",
//        "participants": "全体同学",
//        "introduce": "为了庆祝朱宇翔生日，学校邀请..."
//        },
//
//        // 2. 语义向量数据 (交给 Qdrant)
//        "vector": [0.015, -0.022, 0.089, ...], // 1536维浮点数组
//
//        // 3. 原始切块文本 (存入 Qdrant 的 payload/metadata 中，用于检索后直接返回给 LLM)
//        "chunkText": "【会议】地点：三教；参与：全体同学；内容：开会；补充：为了庆祝朱宇翔生日..."
//