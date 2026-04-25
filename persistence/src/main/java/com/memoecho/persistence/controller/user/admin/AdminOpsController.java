package com.memoecho.persistence.controller.user.admin;

import com.memoecho.common.response.ApiResponse;
import com.memoecho.memo_echo_apis.vo.UnsafeGroupVO;
import com.memoecho.memo_echo_apis.vo.UnsafeMessageVO;
import com.memoecho.persistence.mapper.UserMessageMapper;
import com.memoecho.persistence.pojo.User;
import com.memoecho.persistence.service.BotStatusService;
import com.memoecho.persistence.service.UnsafeMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 🛰️ 处理管理员监控全局的需求
 * <hr/>
 * 🧩 职责：
 * 🛡️ 关联：
 * 🗺️ 架构： (MemoEcho)
 *
 * @author Sisfuzues
 * &#064;date  2026/4/19 22:48
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminOpsController {
    private final UnsafeMessageService unsafeMessageService;
    private final BotStatusService botStatusService;
    private final UserMessageMapper userMessageMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/group/unsafe")
    public ApiResponse<List<UnsafeGroupVO>> getUnsafeGroup(){
        List<UnsafeGroupVO> unsafeGroupInfo = unsafeMessageService.getUnsafeGroupInfo();
        return ApiResponse.success("获取违规群组列表成功", unsafeGroupInfo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/group/{groupId}/unsafe/messages")
    public ApiResponse<List<UnsafeMessageVO>> getUnsafeMessageInfo(@PathVariable Long groupId){
        List<UnsafeMessageVO> unsafeMessageInfo = unsafeMessageService.getUnsafeMessageInfo(groupId);
        return ApiResponse.success("获取违规群组信息成功", unsafeMessageInfo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bot/info")
    public ApiResponse<Map<String,Integer>> getBotInfo(){
        Map<String,Integer> res = botStatusService.getBotInfo();
        return ApiResponse.success("获取机器人状态成功", res);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/user/{userId}/admin")
    public ApiResponse<String> upgradeUserToAdmin(@PathVariable Long userId){
        if (userId == null || userId <= 0) {
            return ApiResponse.fail(400, "userId 非法。");
        }

        User user = userMessageMapper.selectById(userId);
        if (user == null) {
            return ApiResponse.fail(404, "用户不存在。");
        }

        if (user.getUserLevel() == 10) {
            return ApiResponse.success("该用户已经是管理员。", "ok");
        }

        user.setUserLevel(10);
        int updated = userMessageMapper.updateById(user);
        if (updated != 1) {
            return ApiResponse.fail(500, "升级管理员失败。");
        }

        return ApiResponse.success("升级管理员成功。", "ok");
    }
}
