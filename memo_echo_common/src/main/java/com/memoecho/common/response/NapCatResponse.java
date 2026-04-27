package com.memoecho.common.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NapCatResponse<T> {
    private String status;
    private Integer retcode;
    private String message; // 错误信息
    private T data;
}
