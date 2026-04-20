package com.memoecho.bot_gateway.client;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 🛰️ Napcat 远程服务接口
 * <hr/>
 * 🧩 职责： 通过 OpenFeign 调用 napcat 服务
 * 🗺️ 架构： (MemoEcho)
 *
 * @author Sisfuzues
 * @date 2026/4/20 13:23
 */
@FeignClient(name = "napcatClient",url = "${napcat.url}")
public interface NapcatClient {
    // ================= 发送信息类 ===================


    // ================= 获取信息类 ===================
    @GetMapping("/get_login_info")
    JSONObject getLoginInfo();

    // ================= 系统信息类 ===================
    @GetMapping("/get_status")
    JSONObject getStatus();
}
