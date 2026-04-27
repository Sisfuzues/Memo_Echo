package com.memoecho.sensitive_filter.service;

import com.memoecho.memo_echo_apis.dto.ReceivedMessage;

public interface MessageFrequencyService {
    boolean isSpam(ReceivedMessage payload);
}
