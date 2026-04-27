package com.memoecho.sensitive_filter.service;

import com.memoecho.memo_echo_apis.dto.ReceivedMessage;

public interface SensitiveWordsService {
    boolean sensitiveWordsKill(ReceivedMessage msg);

    void handleConsumerMsg(String safeTopic, String safeTags,
                           String unsafeTopic, String unsafeTags,
                           ReceivedMessage msg);
}
