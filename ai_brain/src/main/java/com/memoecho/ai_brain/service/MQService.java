package com.memoecho.ai_brain.service;

import com.memoecho.memo_echo_apis.dto.ExtractedMessage;
import com.memoecho.memo_echo_apis.dto.ReplyMessage;

import java.util.List;

public interface MQService {
    /**
     * 发送MQ信息
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param msg 处理后的消息
     * @param key 消息的key
     * @return boolean 代表是成功发送
     * @author Sisfuzes
     * @date  2026/4/19 22:15
     */
    boolean sendToMQ(ExtractedMessage msg,String key);

}
