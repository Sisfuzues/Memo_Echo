package com.memoecho.memo_echo_apis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MQPayload<T> implements Serializable {   // MQ消息队列的基本信息
    private String eventId; // 事件跟踪唯一Id
    private Long timestamp; // 时间戳
    private T data;    // 具体数据
}
