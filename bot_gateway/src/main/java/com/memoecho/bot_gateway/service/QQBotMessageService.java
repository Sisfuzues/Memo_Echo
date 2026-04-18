package com.memoecho.bot_gateway.service;

public interface QQBotMessageService {
    /**
     * QQbot发送群体信息
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param groupId 用户的ID, txt 字符串类型的消息数据
     * @author Sisfuzues
     * @date 2026/4/13 11:11
     */
    void sendGroupMessage(Long groupId, String txt);


    /**
     * QQbot发送私人信息
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param userId 用户的ID, txt 字符串类型的消息数据
     * @author Sisfuzues
     * @date 2026/4/13 11:11
     */
    void sendPrivateMessage(Long userId, String txt);
}
