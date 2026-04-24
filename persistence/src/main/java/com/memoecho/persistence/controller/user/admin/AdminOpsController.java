package com.memoecho.persistence.controller.user.admin;

import com.memoecho.persistence.pojo.DTO.*;
import com.memoecho.persistence.service.AdminOpsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
@RequestMapping("/ops")
public class AdminOpsController {

    private final AdminOpsService adminOpsService;

    public AdminOpsController(AdminOpsService adminOpsService) {
        this.adminOpsService = adminOpsService;
    }

    // ==================== 机器人 ====================

    @GetMapping("/bots")
    public Map<String, Object> listBots() {
        return success(adminOpsService.listBots());
    }

    @PatchMapping("/bots/{id}/status")
    public Map<String, Object> toggleBotStatus(@PathVariable Long id) {
        Bot bot = adminOpsService.toggleBotStatus(id);

        if (bot == null) {
            return fail("机器人不存在");
        }

        return success("机器人状态修改成功", bot);
    }

    @PostMapping("/bots/refresh")
    public Map<String, Object> refreshBots() {
        return success("机器人状态刷新成功", adminOpsService.refreshBots());
    }

    // ==================== 群聊 ====================

    @GetMapping("/groups")
    public Map<String, Object> listGroups() {
        System.out.println("Handling /ops/groups request");
        return success(adminOpsService.listGroups());
    }

    @PostMapping("/groups/join")
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

    @DeleteMapping("/groups/{id}")
    public Map<String, Object> exitGroup(@PathVariable Long id) {
        Boolean result = adminOpsService.exitGroup(id);

        if (!result) {
            return fail("群聊不存在");
        }

        return success("退出群聊成功", true);
    }

    @PatchMapping("/groups/{id}/sensitive")
    public Map<String, Object> toggleSensitiveMonitor(@PathVariable Long id) {
        GroupChat groupChat = adminOpsService.toggleSensitiveMonitor(id);

        if (groupChat == null) {
            return fail("群聊不存在");
        }

        return success("敏感词监控状态修改成功", groupChat);
    }

    @PatchMapping("/groups/{id}/schedule")
    public Map<String, Object> toggleScheduleMonitor(@PathVariable Long id) {
        GroupChat groupChat = adminOpsService.toggleScheduleMonitor(id);

        if (groupChat == null) {
            return fail("群聊不存在");
        }

        return success("日程监控状态修改成功", groupChat);
    }

    // ==================== 好友 ====================

    @GetMapping("/friends")
    public Map<String, Object> listFriends() {
        return success(adminOpsService.listFriends());
    }

    @PatchMapping("/friends/{id}/agree")
    public Map<String, Object> agreeFriend(@PathVariable Long id) {
        FriendRequest friend = adminOpsService.agreeFriend(id);

        if (friend == null) {
            return fail("好友申请不存在");
        }

        return success("已同意好友", friend);
    }

    @PatchMapping("/friends/{id}/block")
    public Map<String, Object> blockFriend(@PathVariable Long id) {
        FriendRequest friend = adminOpsService.blockFriend(id);

        if (friend == null) {
            return fail("好友不存在");
        }

        return success("已拉黑好友", friend);
    }

    // ==================== 敏感词 ====================

    @GetMapping("/sensitive/words")
    public Map<String, Object> listSensitiveWords() {
        return success(adminOpsService.listSensitiveWords());
    }

    @PostMapping("/sensitive/words")
    public Map<String, Object> addSensitiveWord(@RequestBody SensitiveWordDTO dto) {

        if (dto.getWord() == null || dto.getWord().trim().isEmpty()) {
            return fail("敏感词不能为空");
        }

        return success("敏感词添加成功", adminOpsService.addSensitiveWord(dto.getWord()));
    }

    @DeleteMapping("/sensitive/words/{word}")
    public Map<String, Object> deleteSensitiveWord(@PathVariable String word) {
        return success("敏感词删除成功", adminOpsService.deleteSensitiveWord(word));
    }

    @GetMapping("/sensitive/records")
    public Map<String, Object> listSensitiveRecords() {
        return success(adminOpsService.listSensitiveRecords());
    }

    // ==================== 日程 ====================

    @GetMapping("/schedules")
    public Map<String, Object> listSchedules() {
        return success(adminOpsService.listSchedules());
    }

    @PatchMapping("/schedules/{id}/handle")
    public Map<String, Object> handleSchedule(@PathVariable Long id) {
        ScheduleTask task = adminOpsService.handleSchedule(id);

        if (task == null) {
            return fail("日程任务不存在");
        }

        return success("日程处理成功", task);
    }

    @DeleteMapping("/schedules/{id}")
    public Map<String, Object> deleteSchedule(@PathVariable Long id) {
        Boolean result = adminOpsService.deleteSchedule(id);

        if (!result) {
            return fail("日程任务不存在");
        }

        return success("日程删除成功", true);
    }

    // ==================== 日志 ====================

    @GetMapping("/logs")
    public Map<String, Object> listLogs() {
        return success(adminOpsService.listLogs());
    }

    @DeleteMapping("/logs")
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
