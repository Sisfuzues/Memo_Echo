package com.memoecho.bot_gateway.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.memoecho.bot_gateway.client.NapcatClient;
import com.memoecho.bot_gateway.dto.BotGroupRequestDTO;
import com.memoecho.bot_gateway.dto.BotGroupRequestResult;
import com.memoecho.bot_gateway.dto.BotSendTextRequest;
import com.memoecho.bot_gateway.service.BotGroupOpsService;
import com.memoecho.bot_gateway.service.Impl.QQBotMessageServiceImpl;
import com.memoecho.common.response.ApiResponse;
import com.memoecho.common.response.NapCatResponse;
import com.memoecho.memo_echo_apis.dto.ResponseMessage;
import com.memoecho.memo_echo_apis.vo.FriendInfoVO;
import com.memoecho.memo_echo_apis.vo.GroupInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 🛰️ 处理 bot 的各种任务
 * <hr/>
 * 🧩 职责：
 * 🛡️ 关联：
 * 🗺️ 架构： (MemoEcho)
 *
 * @author Sisfuzues
 * @date  2026/4/19 22:50
 */
@Slf4j(topic = "BotReader")
@RestController
@RequestMapping("/bot")
@RequiredArgsConstructor
public class QQBotController {
    private final QQBotMessageServiceImpl botMessageService;
    private final NapcatClient napcatClient;
    private final BotGroupOpsService botGroupOpsService;

    /**
     *  获取机器人的所有信息
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param message JSONObject格式的信息
     * @return String 类型的信息，告诉NapCat有没有接收成功
     * @author Sisfuzues
     * &#064;date  2026/4/13 14:15
     */
    @PostMapping("/webhook")
    public String readMessage(@RequestBody String message){
        log.info("收到原始信息：{}",message);

        JSONObject jsonObject = JSON.parseObject(message);
        String type = jsonObject.getString("post_type");

        if(type == null){
            log.warn("获取到未知类型消息。消息内容:{}",message);
            return "ignore";
        }

        botMessageService.processBotEvent(jsonObject,type);
        return "{}";
    }

    @PostMapping("/send/response")
    public ApiResponse<String> sendResponse(@RequestBody ResponseMessage responseMessage){
        if (responseMessage == null || responseMessage.getGroupId() == null) {
            return ApiResponse.fail(400, "groupId 不能为空，且该接口当前只用于群消息发送。");
        }
        if (responseMessage.getResponse() == null || responseMessage.getResponse().isEmpty()) {
            return ApiResponse.fail(400, "response 不能为空。");
        }
        botMessageService.sendGroupResponse(responseMessage);
        return ApiResponse.success("广播成功。", "ok");
    }

    @PostMapping("/send/text")
    public ApiResponse<String> sendText(@RequestBody BotSendTextRequest request) {
        if (request == null) {
            return ApiResponse.fail(400, "请求体不能为空。");
        }

        String content = request.getContent() == null ? "" : request.getContent().trim();
        boolean hasGroupTarget = request.getGroupId() != null;
        boolean hasPrivateTarget = request.getPrivateId() != null;

        if (!hasGroupTarget && !hasPrivateTarget) {
            return ApiResponse.fail(400, "groupId 和 privateId 至少要提供一个。");
        }

        if (hasGroupTarget && hasPrivateTarget) {
            return ApiResponse.fail(400, "groupId 和 privateId 不能同时提供。");
        }

        if (content.isEmpty()) {
            return ApiResponse.fail(400, "发送内容不能为空。");
        }

        if (hasGroupTarget) {
            botMessageService.sendGroupMessage(request.getGroupId(), content);
            return ApiResponse.success("群消息发送成功。", "ok");
        }

        botMessageService.sendPrivateMessage(request.getPrivateId(), content);
        return ApiResponse.success("私聊消息发送成功。", "ok");
    }

    @GetMapping("/group/list")
    public ApiResponse<List<GroupInfoVO>> getGroupInfo(){
        NapCatResponse<List<GroupInfoVO>> groupList = napcatClient.getGroupList();
        if (groupList == null || groupList.getData() == null) {
            return ApiResponse.fail(502, "获取群列表失败。");
        }
        return ApiResponse.success(groupList.getData());
    }

    @GetMapping("/friend/list")
    public ApiResponse<List<FriendInfoVO>> getFriendInfo(){
        NapCatResponse<List<FriendInfoVO>> friendList = napcatClient.getFriendList();
        if (friendList == null || friendList.getData() == null) {
            return ApiResponse.fail(502, "获取好友列表失败。");
        }
        return ApiResponse.success(friendList.getData());
    }

    @PostMapping("/group/request")
    public ApiResponse<BotGroupRequestResult> handleGroupRequest(@RequestBody BotGroupRequestDTO request) {
        if (request == null) {
            return ApiResponse.fail(400, "请求体不能为空。");
        }

        if ((request.getGroupId() == null || request.getGroupId() <= 0)
                && (request.getFlag() == null || request.getFlag().trim().isEmpty())) {
            return ApiResponse.fail(400, "groupId 和 flag 至少要提供一个。");
        }

        BotGroupRequestResult result = botGroupOpsService.handleGroupRequest(request);
        if ("ALREADY_IN_GROUP".equals(result.getStatus())
                || "REQUEST_HANDLED".equals(result.getStatus())
                || "NEED_INVITE".equals(result.getStatus())) {
            return ApiResponse.success(result.getMessage(), result);
        }

        return ApiResponse.fail(409, result.getMessage());
    }

    @DeleteMapping("/group/{groupId}")
    public ApiResponse<String> deleteGroup(
            @PathVariable("groupId") Long groupId,
            @RequestParam(value = "isDismiss", defaultValue = "false") Boolean isDismiss
    ){
        if (groupId == null || groupId <= 0) {
            return ApiResponse.fail(400, "groupId 非法。");
        }

        Map<String,Object> params = new HashMap<>();
        params.put("group_id",groupId);
        params.put("is_dismiss", isDismiss);
        NapCatResponse<Void> voidNapCatResponse
                = napcatClient.setGroupLeave(params);
        if (voidNapCatResponse == null || voidNapCatResponse.getRetcode() == null) {
            return ApiResponse.fail(502,"离开群聊失败，NapCat 未返回有效结果。");
        }
        Integer retcode = voidNapCatResponse.getRetcode();
        if(retcode!=0){
            return ApiResponse.fail(retcode,"离开群聊失败。");
        }
        return ApiResponse.success("成功离开群聊。");
    }

    @DeleteMapping("/friend/{userId}")
    public ApiResponse<String> deleteFriend(@PathVariable("userId") Long userId){
        if (userId == null || userId <= 0) {
            return ApiResponse.fail(400, "userId 非法。");
        }

        Map<String,Object> params = new HashMap<>();
        params.put("user_id",userId);
        NapCatResponse<Void> voidNapCatResponse
                = napcatClient.deleteFriend(params);
        if (voidNapCatResponse == null || voidNapCatResponse.getRetcode() == null) {
            return ApiResponse.fail(502,"删除好友失败，NapCat 未返回有效结果。");
        }
        Integer retcode = voidNapCatResponse.getRetcode();
        if(retcode!=0){
            return ApiResponse.fail(retcode,"删除好友失败。");
        }
        return ApiResponse.success("成功删除好友。");
    }
}
