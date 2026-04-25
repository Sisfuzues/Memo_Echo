package com.memoecho.persistence.controller.user.admin;

import com.memoecho.common.response.ApiResponse;
import com.memoecho.memo_echo_apis.vo.UnsafeGroupVO;
import com.memoecho.memo_echo_apis.vo.UnsafeMessageVO;
import com.memoecho.persistence.dto.ScheduleGroupRequestVO;
import com.memoecho.persistence.mapper.UserMessageMapper;
import com.memoecho.persistence.pojo.DTO.Bot;
import com.memoecho.persistence.pojo.DTO.FriendRequest;
import com.memoecho.persistence.pojo.DTO.GroupChat;
import com.memoecho.persistence.pojo.DTO.GroupJoinDTO;
import com.memoecho.persistence.pojo.DTO.OperationLog;
import com.memoecho.persistence.pojo.DTO.ScheduleTask;
import com.memoecho.persistence.pojo.User;
import com.memoecho.persistence.service.AdminOpsService;
import com.memoecho.persistence.service.BotStatusService;
import com.memoecho.persistence.service.UnsafeMessageService;
import com.memoecho.persistence.service.UserOpsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 🛰️ 管理员操作处理
 * <hr/>
 * 🧩 职责： 处理用户对于机器人的请求
 * 🛡️ 关联：
 * 🗺️ 架构： (MemoEcho)
 *
 * @author Jack
 * &#064;date  2026/4/19 22:46
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AdminOpsController {
    private final UnsafeMessageService unsafeMessageService;
    private final BotStatusService botStatusService;
    private final UserMessageMapper userMessageMapper;
    private final AdminOpsService adminOpsService;
    private final UserOpsService userOpsService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/group/unsafe")
    public ApiResponse<List<UnsafeGroupVO>> getUnsafeGroup() {
        List<UnsafeGroupVO> unsafeGroupInfo = unsafeMessageService.getUnsafeGroupInfo();
        return ApiResponse.success("获取违规群组列表成功", unsafeGroupInfo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/group/{groupId}/unsafe/messages")
    public ApiResponse<List<UnsafeMessageVO>> getUnsafeMessageInfo(@PathVariable Long groupId) {
        List<UnsafeMessageVO> unsafeMessageInfo = unsafeMessageService.getUnsafeMessageInfo(groupId);
        return ApiResponse.success("获取违规群组信息成功", unsafeMessageInfo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/bot/info")
    public ApiResponse<Map<String, Integer>> getBotInfo() {
        Map<String, Integer> res = botStatusService.getBotInfo();
        return ApiResponse.success("获取机器人状态成功", res);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/schedule/group/requests")
    public ApiResponse<List<ScheduleGroupRequestVO>> listScheduleGroupRequests() {
        List<ScheduleGroupRequestVO> requests = userOpsService.listScheduleGroupRequests();
        return ApiResponse.success("获取群日程管理申请成功", requests);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/user/{userId}/admin")
    public ApiResponse<String> upgradeUserToAdmin(@PathVariable Long userId) {
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

    // ==================== 机器人 ====================

    @GetMapping("/ops/bots")
    public Map<String, Object> listBots() {
        return success(adminOpsService.listBots());
    }

    @PatchMapping("/ops/bots/{id}/status")
    public Map<String, Object> toggleBotStatus(@PathVariable Long id) {
        Bot bot = adminOpsService.toggleBotStatus(id);

        if (bot == null) {
            return fail("机器人不存在");
        }

        return success("机器人状态修改成功", bot);
    }

    @PostMapping("/ops/bots/refresh")
    public Map<String, Object> refreshBots() {
        return success("机器人状态刷新成功", adminOpsService.refreshBots());
    }

    // ==================== 群聊 ====================

    @GetMapping("/ops/groups")
    public Map<String, Object> listGroups() {
        return success(adminOpsService.listGroups());
    }

    @PostMapping("/ops/groups/join")
    public Map<String, Object> joinGroup(@RequestBody GroupJoinDTO dto) {
        if (dto.getName() == null || dto.getGroupId() == null || dto.getBotName() == null) {
            return fail("群聊信息不完整");
        }

        GroupChat groupChat = new GroupChat();
        groupChat.setName(dto.getName());
        groupChat.setGroupId(dto.getGroupId());
        groupChat.setBotName(dto.getBotName());

        return success("进入群聊成功", adminOpsService.joinGroup(groupChat));
    }

    @DeleteMapping("/ops/groups/{id}")
    public Map<String, Object> exitGroup(@PathVariable Long id) {
        Boolean result = adminOpsService.exitGroup(id);

        if (!result) {
            return fail("群聊不存在");
        }

        return success("退出群聊成功", true);
    }

    @PatchMapping("/ops/groups/{id}/sensitive")
    public Map<String, Object> toggleSensitiveMonitor(@PathVariable Long id) {
        GroupChat groupChat = adminOpsService.toggleSensitiveMonitor(id);

        if (groupChat == null) {
            return fail("群聊不存在");
        }

        return success("敏感词监控状态修改成功", groupChat);
    }

    @PatchMapping("/ops/groups/{id}/schedule")
    public Map<String, Object> toggleScheduleMonitor(@PathVariable Long id) {
        GroupChat groupChat = adminOpsService.toggleScheduleMonitor(id);

        if (groupChat == null) {
            return fail("群聊不存在");
        }

        return success("日程监控状态修改成功", groupChat);
    }

    // ==================== 好友 ====================

    @GetMapping("/ops/friends")
    public Map<String, Object> listFriends() {
        return success(adminOpsService.listFriends());
    }

    @PatchMapping("/ops/friends/{id}/agree")
    public Map<String, Object> agreeFriend(@PathVariable Long id) {
        FriendRequest friend = adminOpsService.agreeFriend(id);

        if (friend == null) {
            return fail("好友申请不存在");
        }

        return success("已同意好友", friend);
    }

    @PatchMapping("/ops/friends/{id}/block")
    public Map<String, Object> blockFriend(@PathVariable Long id) {
        FriendRequest friend = adminOpsService.blockFriend(id);

        if (friend == null) {
            return fail("好友不存在");
        }

        return success("已拉黑好友", friend);
    }

    // ==================== 敏感词 ====================

    @GetMapping("/ops/sensitive/words")
    public Map<String, Object> listSensitiveWords() {
        return success(adminOpsService.listSensitiveWords());
    }

    @PostMapping("/ops/sensitive/words")
    public Map<String, Object> addSensitiveWord(@RequestBody Map<String, String> dto) {
        String word = dto.get("word");
        if (word == null || word.trim().isEmpty()) {
            return fail("敏感词不能为空");
        }

        return success("敏感词添加成功", adminOpsService.addSensitiveWord(word));
    }

    @DeleteMapping("/ops/sensitive/words/{word}")
    public Map<String, Object> deleteSensitiveWord(@PathVariable String word) {
        return success("敏感词删除成功", adminOpsService.deleteSensitiveWord(word));
    }

    @GetMapping("/ops/sensitive/records")
    public Map<String, Object> listSensitiveRecords() {
        return success(adminOpsService.listSensitiveRecords());
    }

    // ==================== 日程 ====================

    @GetMapping("/ops/schedules")
    public Map<String, Object> listSchedules() {
        return success(adminOpsService.listSchedules());
    }

    @PatchMapping("/ops/schedules/{id}/handle")
    public Map<String, Object> handleSchedule(@PathVariable Long id) {
        ScheduleTask task = adminOpsService.handleSchedule(id);

        if (task == null) {
            return fail("日程任务不存在");
        }

        return success("日程处理成功", task);
    }

    @DeleteMapping("/ops/schedules/{id}")
    public Map<String, Object> deleteSchedule(@PathVariable Long id) {
        Boolean result = adminOpsService.deleteSchedule(id);

        if (!result) {
            return fail("日程任务不存在");
        }

        return success("日程删除成功", true);
    }

    // ==================== 日志 ====================

    @GetMapping("/ops/logs")
    public Map<String, Object> listLogs() {
        List<OperationLog> operationLogs = adminOpsService.listLogs();
        return success(operationLogs);
    }

    @DeleteMapping("/ops/logs")
    public Map<String, Object> clearLogs() {
        return success("日志清空成功", adminOpsService.clearLogs());
    }

    // ==================== 通用返回 ====================

    private Map<String, Object> success(Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "success");
        map.put("data", data);
        return map;
    }

    private Map<String, Object> success(String message, Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", message);
        map.put("data", data);
        return map;
    }

    private Map<String, Object> fail(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 400);
        map.put("message", message);
        map.put("data", null);
        return map;
    }
}
