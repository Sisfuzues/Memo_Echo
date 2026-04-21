package com.memoecho.persistence.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CodeDetails {
    private String email;
    private String code;
    private Long expirationTime;
}
