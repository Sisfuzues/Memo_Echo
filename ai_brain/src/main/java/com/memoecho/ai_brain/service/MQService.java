package com.memoecho.ai_brain.service;

import com.memoecho.memo_echo_apis.dto.ReplyMessage;

public interface MQService {
    /**
     * 发送MQ信息
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param
     * @param msg
     * @return
     * @author &#064;date  2026/4/19 22:15
     */
    boolean sendToMQ(ReplyMessage msg);
}
