package com.memoecho.persistence.service;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

public interface BotStatusService {
    /**
     *  设置机器人状态
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：status 1为在线，0为下线
     *
     * @param  botName 机器人的名字
     * @param  status 机器人的状态
     * @return 是否成功更改
     * @author Sisfuzues
     * @date 2026/4/20 14:45
     */
    Boolean setBotStatus(String botName,int status);

    Map<String,Integer> getBotInfo();
}
