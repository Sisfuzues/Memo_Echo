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

    public static class Constant{
        public static final int DANGER_SCORE = 5; // 危险分数
    }
}
