package com.memoecho.bot_gateway.service;

import com.memoecho.memo_echo_apis.dto.ReceivedMessage;

public interface MQService {
    /**
     *  发送记录到MQ队列里
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param msg 需要传输的信息
     * @param topic 需要传输的topic
     * @param tag 需要传输的tag
     * @param key 需要传输的key
     * @author Sisfuzues
     * @return Boolean
     * &#064;date  2026/4/18 00:27
     */
    Boolean sendToMQ(ReceivedMessage msg,String topic,String tag,String key);
}
