package com.memoecho.persistence.pojo.DTO;

public class Bot {
    private Long id;
    private String name;
    private String botId;
    private String status;
    private Integer groupCount;
    private String lastHeartbeat;

    public Bot() {}

    public Bot(Long id, String name, String botId, String status, Integer groupCount, String lastHeartbeat) {
        this.id = id;
        this.name = name;
        this.botId = botId;
        this.status = status;
        this.groupCount = groupCount;
        this.lastHeartbeat = lastHeartbeat;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBotId() {
        return botId;
    }

    public String getStatus() {
        return status;
    }

    public Integer getGroupCount() {
        return groupCount;
    }

    public String getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBotId(String botId) {
        this.botId = botId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setGroupCount(Integer groupCount) {
        this.groupCount = groupCount;
    }

    public void setLastHeartbeat(String lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }
}