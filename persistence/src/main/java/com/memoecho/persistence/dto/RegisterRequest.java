package com.memoecho.persistence.dto;


import lombok.Data;

@Data
public class RegisterRequest {
    private Long UserId;
    private String password;
    private String email;
}
