package com.memoecho.ai_brain.service;

import com.memoecho.memo_echo_apis.dto.ExtractedMessage;
import com.memoecho.memo_echo_apis.dto.ReceivedMessage;

public interface UnextractedMsgService {
    /**
     *  处理未处理的消息
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param msg 就是未通过AI处理的原始信息
     * @return ExtractedMessage 已经处理好的回复信息
     * @author zhuyuxiang
     * @date 2026/4/19 22:12
     */
    ExtractedMessage savedMsg(ReceivedMessage msg);
}
