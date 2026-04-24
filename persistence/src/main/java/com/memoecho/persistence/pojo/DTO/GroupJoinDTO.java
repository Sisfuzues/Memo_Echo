package com.memoecho.persistence.pojo.DTO;

public class GroupJoinDTO {
    private String name;
    private String groupId;
    private String botName;

    public String getName() {
        return name;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getBotName() {
        return botName;
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
}
