package com.memoecho.persistence.pojo.DTO;

public class GroupChat {
    private Long id;
    private String name;
    private String groupId;
    private String botName;
    private Boolean sensitiveMonitor;
    private Boolean scheduleMonitor;

    public GroupChat() {}

    public GroupChat(Long id, String name, String groupId, String botName, Boolean sensitiveMonitor, Boolean scheduleMonitor) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
        this.botName = botName;
        this.sensitiveMonitor = sensitiveMonitor;
        this.scheduleMonitor = scheduleMonitor;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getBotName() {
        return botName;
    }

    public Boolean getSensitiveMonitor() {
        return sensitiveMonitor;
    }

    public Boolean getScheduleMonitor() {
        return scheduleMonitor;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public void setSensitiveMonitor(Boolean sensitiveMonitor) {
        this.sensitiveMonitor = sensitiveMonitor;
    }

    public void setScheduleMonitor(Boolean scheduleMonitor) {
        this.scheduleMonitor = scheduleMonitor;
    }
}