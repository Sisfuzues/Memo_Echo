package com.memoecho.sensitive_filter.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SensitiveWords implements Serializable {
    private Level level;
    private int value;
    public SensitiveWords(Level level,int value){
        this.level = level;
        this.value = value;
    }

    public enum Level {
        level1,level2,level3
    }
}
