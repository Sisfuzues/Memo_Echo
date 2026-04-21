package com.memoecho.ai_brain.service.Impl;

import com.memoecho.ai_brain.service.MQService;
import com.memoecho.memo_echo_apis.dto.ExtractedMessage;
import org.springframework.stereotype.Service;

@Service
public class MQServiceImpl implements MQService {
    @Override
    public boolean sendToMQ(ExtractedMessage msg, String key) {
        return false;
    }
}
