package com.memoecho.bot_gateway.task;

public interface BotStatusTask {

    /**
     *  检查机器人是否存活
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意： 这个必须要实现成定时任务,如果是websocket链接，请忽略。
     *
     * @author Sisfuzues
     * @date 2026/4/20 20:37
     */
    void checkHealth();
}
