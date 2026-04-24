package com.memoecho.persistence.service;

import com.memoecho.persistence.pojo.DTO.*;

import java.util.List;

public interface AdminOpsService {
    List<Bot> listBots();

    Bot toggleBotStatus(Long id);

    List<Bot> refreshBots();

    List<GroupChat> listGroups();

    GroupChat joinGroup(GroupChat groupChat);

    Boolean exitGroup(Long id);

    GroupChat toggleSensitiveMonitor(Long id);

    GroupChat toggleScheduleMonitor(Long id);

    List<FriendRequest> listFriends();

    FriendRequest agreeFriend(Long id);

    FriendRequest blockFriend(Long id);

    List<String> listSensitiveWords();

    List<String> addSensitiveWord(String word);

    List<String> deleteSensitiveWord(String word);

    List<SensitiveRecord> listSensitiveRecords();

    List<ScheduleTask> listSchedules();

    ScheduleTask handleSchedule(Long id);

    Boolean deleteSchedule(Long id);

    List<OperationLog> listLogs();

    Boolean clearLogs();
}