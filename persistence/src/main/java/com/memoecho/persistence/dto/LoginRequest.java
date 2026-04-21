package com.memoecho.persistence.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private Long userId;
    private String password;
}
