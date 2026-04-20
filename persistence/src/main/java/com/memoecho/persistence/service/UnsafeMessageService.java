package com.memoecho.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import com.memoecho.persistence.pojo.UnsafeMessage;

public interface UnsafeMessageService extends IService<UnsafeMessage> {
    /**
     *  存储不安全的消息，更新群安全阈值
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param  msg 需要处理的message信息
     * @return Boolean 代表是否成功处理
     * @author Sisfuzues
     * &#064;date  2026/4/19 15:12
     */
    boolean updateMsgAndScore(UnsafeMessage msg);
}
