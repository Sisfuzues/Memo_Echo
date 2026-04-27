package com.memoecho.persistence.pojo.DTO;

public class ScheduleTask {
    private Long id;
    private String groupName;
    private String message;
    private String scheduleTitle;
    private String scheduleTime;
    private String status;

    public ScheduleTask() {}

    public ScheduleTask(Long id, String groupName, String message, String scheduleTitle, String scheduleTime, String status) {
        this.id = id;
        this.groupName = groupName;
        this.message = message;
        this.scheduleTitle = scheduleTitle;
        this.scheduleTime = scheduleTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getMessage() {
        return message;
    }

    public String getScheduleTitle() {
        return scheduleTitle;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setScheduleTitle(String scheduleTitle) {
        this.scheduleTitle = scheduleTitle;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}