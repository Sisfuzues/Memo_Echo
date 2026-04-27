package com.memoecho.bot_gateway.client;

import com.alibaba.fastjson2.JSONObject;
import com.memoecho.common.response.NapCatResponse;
import com.memoecho.memo_echo_apis.vo.FriendInfoVO;
import com.memoecho.memo_echo_apis.vo.GroupInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

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
    /**
     * 🐊 发送私人消息
     * <hr/>
     * 🥹 注意:
     * <pre>
     * params: user_id (Long), message (String/Array)
     * </pre>
     *
     * @return JSONObject
     * @author Sisfuzues
     * @Date 2026/4/21 11:09
     */
    @PostMapping("/send_private_msg")
    JSONObject sendPrivateMsg(@RequestBody Map<String, Object> params);

    /**
     * 🐊 发送群聊消息
     * <hr/>
     * 🥹 注意:
     * <pre>
     * params: group_id (Long), message (String/Array)
     * </pre>
     *
     * @return JSONObject
     * @author Sisfuzues
     * @Date 2026/4/21 11:09
     */
    @PostMapping("/send_group_msg")
    JSONObject sendGroupMsg(@RequestBody Map<String, Object> params);

    /**
     * 🐊 撤回消息
     * <hr/>
     * 🥹 注意:
     * <pre>
     * params: message_id (Integer)
     * 相当于撤回机器人在这个群的上一条发的信息。
     * </pre>
     *
     * @return JSONObject
     * @author Sisfuzues
     * @Date 2026/4/21 11:10
     */
    @PostMapping("/delete_msg")
    JSONObject deleteMsg(@RequestBody Map<String, Object> params);

    // ================= 获取信息类 ===================
    /**
     * 🐊 获取机器人状况
     * <hr/>
     * 🥹 注意:
     * <pre>
     * 可以监控获取机器人的登录状况
     * </pre>
     * @return com.alibaba.fastjson2.JSONObject
     * @author Sisfuzues
     * @Date 2026/4/21 11:06
     */
    @GetMapping("/get_login_info")
    JSONObject getLoginInfo();

    /**
     * 🐊 获取机器人加的所有群
     * <hr/>
     * 🥹 注意:
     * @return JSONObject
     * @author Sisfuzues
     * @Date 2026/4/21 11:07
     */
    @PostMapping("/get_group_list")
    NapCatResponse<List<GroupInfoVO>> getGroupList();

    @PostMapping("/get_friend_list")
    NapCatResponse<List<FriendInfoVO>> getFriendList();

    // ================= 系统信息类 ===================
    @GetMapping("/get_status")
    JSONObject getStatus();


    // ================= 关系增删类 ===================

    /**
     * 🐊 控制机器人退群
     * <hr/>
     * 🥹 注意:
     * <pre>
     * params:
     * - group_id (Long): 群号
     * - is_dismiss (Boolean):
     *      是否解散群。如果机器人是群主，设为 true 会解散群，
     *      设为 false 会退出并转移群主。普通群员此参数无效。
     * </pre>
     * @Param [java.util.Map<java.lang.String,java.lang.Object>] [params]
     * @Return com.alibaba.fastjson2.JSONObject
     * @Author
     * @Date 2026/4/21 10:35
     */
    @PostMapping("/set_group_leave")
    NapCatResponse<Void> setGroupLeave(@RequestBody Map<String,Object> params);

    @PostMapping("/set_group_add_request")
    NapCatResponse<Void> setGroupAddRequest(@RequestBody Map<String,Object> params);


    /**
     * 🐊 控制机器人删除好友
     * <hr/>
     * 🥹 注意:
     * @param params
     * @return com.alibaba.fastjson2.JSONObject
     * @author Sisfuzues
     * @Date 2026/4/21 10:54
     */
    @PostMapping("/delete_friend")
    NapCatResponse<Void> deleteFriend(@RequestBody Map<String,Object> params);
}
