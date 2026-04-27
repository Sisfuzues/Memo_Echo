package com.memoecho.persistence.service.Impl;

import com.memoecho.persistence.pojo.DTO.*;
import com.memoecho.persistence.service.AdminOpsService;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminOpsServiceImpl implements AdminOpsService {

    private final List<Bot> botList = new ArrayList<>();
    private final List<GroupChat> groupList = new ArrayList<>();
    private final List<FriendRequest> friendList = new ArrayList<>();
    private final List<String> sensitiveWords = new ArrayList<>();
    private final List<SensitiveRecord> sensitiveRecords = new ArrayList<>();
    private final List<ScheduleTask> scheduleTasks = new ArrayList<>();
    private final List<OperationLog> logs = new ArrayList<>();

    public AdminOpsServiceImpl() {
        botList.add(new Bot(1L, "Memo 日程机器人", "memo-bot-001", "online", 2, "刚刚"));
        botList.add(new Bot(2L, "Echo 回复机器人", "echo-bot-002", "offline", 1, "10分钟前"));

        groupList.add(new GroupChat(1L, "软件工程课程群", "group-10001", "Memo 日程机器人", true, true));
        groupList.add(new GroupChat(2L, "项目测试群", "group-10002", "Echo 回复机器人", true, false));

        friendList.add(new FriendRequest(1L, "张三", "user-001", "申请使用日程机器人", "pending"));
        friendList.add(new FriendRequest(2L, "李四", "user-002", "项目组成员", "accepted"));

        sensitiveWords.add("危险指令");
        sensitiveWords.add("恶意链接");
        sensitiveWords.add("违规内容");

        sensitiveRecords.add(new SensitiveRecord(1L, "21:30:12", "软件工程课程群", "恶意链接", "检测到疑似恶意链接消息"));

        scheduleTasks.add(new ScheduleTask(1L, "软件工程课程群", "明天下午三点开项目会议", "项目会议", "明天 15:00", "pending"));
        scheduleTasks.add(new ScheduleTask(2L, "项目测试群", "周五晚上提交测试报告", "提交测试报告", "周五 晚上", "done"));

        addLog("SYSTEM", "系统初始化完成");
    }

    private void addLog(String type, String content) {
        logs.add(0, new OperationLog(
                System.currentTimeMillis(),
                LocalTime.now().withNano(0).toString(),
                type,
                content
        ));
    }

    @Override
    public List<Bot> listBots() {
        return botList;
    }

    @Override
    public Bot toggleBotStatus(Long id) {
        for (Bot bot : botList) {
            if (bot.getId().equals(id)) {
                String newStatus = "online".equals(bot.getStatus()) ? "offline" : "online";
                bot.setStatus(newStatus);
                bot.setLastHeartbeat("刚刚");
                addLog("BOT", bot.getName() + " 已" + ("online".equals(newStatus) ? "上线" : "下线"));
                return bot;
            }
        }
        return null;
    }

    @Override
    public List<Bot> refreshBots() {
        for (Bot bot : botList) {
            bot.setLastHeartbeat("刚刚");
        }
        addLog("BOT", "已刷新所有机器人在线状态");
        return botList;
    }

    @Override
    public List<GroupChat> listGroups() {
        return groupList;
    }

    @Override
    public GroupChat joinGroup(GroupChat groupChat) {
        groupChat.setId(System.currentTimeMillis());
        groupChat.setSensitiveMonitor(true);
        groupChat.setScheduleMonitor(true);
        groupList.add(groupChat);

        for (Bot bot : botList) {
            if (bot.getName().equals(groupChat.getBotName())) {
                bot.setGroupCount(bot.getGroupCount() + 1);
            }
        }

        addLog("GROUP", "机器人进入群聊：" + groupChat.getName());
        return groupChat;
    }

    @Override
    public Boolean exitGroup(Long id) {
        GroupChat target = null;

        for (GroupChat group : groupList) {
            if (group.getId().equals(id)) {
                target = group;
                break;
            }
        }

        if (target == null) {
            return false;
        }

        groupList.remove(target);

        for (Bot bot : botList) {
            if (bot.getName().equals(target.getBotName()) && bot.getGroupCount() > 0) {
                bot.setGroupCount(bot.getGroupCount() - 1);
            }
        }

        addLog("GROUP", "机器人退出群聊：" + target.getName());
        return true;
    }

    @Override
    public GroupChat toggleSensitiveMonitor(Long id) {
        for (GroupChat group : groupList) {
            if (group.getId().equals(id)) {
                group.setSensitiveMonitor(!group.getSensitiveMonitor());
                addLog("FILTER", group.getName() + " 已" + (group.getSensitiveMonitor() ? "开启" : "关闭") + "敏感词监控");
                return group;
            }
        }
        return null;
    }

    @Override
    public GroupChat toggleScheduleMonitor(Long id) {
        for (GroupChat group : groupList) {
            if (group.getId().equals(id)) {
                group.setScheduleMonitor(!group.getScheduleMonitor());
                addLog("SCHEDULE", group.getName() + " 已" + (group.getScheduleMonitor() ? "开启" : "关闭") + "日程监控");
                return group;
            }
        }
        return null;
    }

    @Override
    public List<FriendRequest> listFriends() {
        return friendList;
    }

    @Override
    public FriendRequest agreeFriend(Long id) {
        for (FriendRequest friend : friendList) {
            if (friend.getId().equals(id)) {
                friend.setStatus("accepted");
                addLog("FRIEND", "已同意好友申请：" + friend.getNickname());
                return friend;
            }
        }
        return null;
    }

    @Override
    public FriendRequest blockFriend(Long id) {
        for (FriendRequest friend : friendList) {
            if (friend.getId().equals(id)) {
                friend.setStatus("blocked");
                addLog("FRIEND", "已拉黑好友：" + friend.getNickname());
                return friend;
            }
        }
        return null;
    }

    @Override
    public List<String> listSensitiveWords() {
        return sensitiveWords;
    }

    @Override
    public List<String> addSensitiveWord(String word) {
        sensitiveWords.add(word);
        addLog("FILTER", "新增敏感词：" + word);
        return sensitiveWords;
    }

    @Override
    public List<String> deleteSensitiveWord(String word) {
        sensitiveWords.remove(word);
        addLog("FILTER", "删除敏感词：" + word);
        return sensitiveWords;
    }

    @Override
    public List<SensitiveRecord> listSensitiveRecords() {
        return sensitiveRecords;
    }

    @Override
    public List<ScheduleTask> listSchedules() {
        return scheduleTasks;
    }

    @Override
    public ScheduleTask handleSchedule(Long id) {
        for (ScheduleTask task : scheduleTasks) {
            if (task.getId().equals(id)) {
                task.setStatus("done");
                addLog("SCHEDULE", "已处理群日程：" + task.getScheduleTitle());
                return task;
            }
        }
        return null;
    }

    @Override
    public Boolean deleteSchedule(Long id) {
        ScheduleTask target = null;

        for (ScheduleTask task : scheduleTasks) {
            if (task.getId().equals(id)) {
                target = task;
                break;
            }
        }

        if (target == null) {
            return false;
        }

        scheduleTasks.remove(target);
        addLog("SCHEDULE", "删除群日程记录：" + target.getScheduleTitle());
        return true;
    }

    @Override
    public List<OperationLog> listLogs() {
        return logs;
    }

    @Override
    public Boolean clearLogs() {
        logs.clear();
        return true;
    }
}