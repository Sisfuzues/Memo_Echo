package com.memoecho.sensitive_filter.service;

import com.memoecho.memo_echo_apis.dto.ReceivedMessage;

public interface SensitiveWordsService {
    /**
     *  敏感词滤网，会统计敏感词得分，5分斩杀并通报。
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param msg 代表着需要筛选的聊天记录
     * @return Boolean类型，代表是否被斩杀。
     * @author Sisfuzues
     * &#064;date  2026/4/18 19:35
     */
    boolean sensitiveWordsKill(ReceivedMessage msg);
}
