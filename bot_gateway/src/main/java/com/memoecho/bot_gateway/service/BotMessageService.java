package com.memoecho.bot_gateway.service;

import com.alibaba.fastjson2.JSONObject;
import com.memoecho.memo_echo_apis.dto.ResponseMessage;

public interface BotMessageService {
    /**
     * bot发送群体信息
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
     * bot发送私人信息
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

    /**
     *  处理机器人信息的类型
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param  jsonObject 接受到的Json对象
     * @param  type Json对象中的，消息类型
     * @author Sisfuzues
     * @date 2026/4/20 16:02
     */
    void processBotEvent(JSONObject jsonObject,String type);

    void sendGroupResponse(ResponseMessage responseMessage);
}
