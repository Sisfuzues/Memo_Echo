package com.memoecho.sensitive_filter.service;

public interface MessageFrequencyService {
    /**
     * 判定消息是否属于恶意重复刷屏
     * @param groupId 群号
     * @param rawMessage 原始消息内容
     * @return true: 是刷屏，需要拦截；false: 正常消息
     */
    boolean isSpam(Long groupId,String rawMessage);
}
