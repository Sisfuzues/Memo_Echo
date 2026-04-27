package com.memoecho.persistence.pojo.DTO;

public class OperationLog {
    private Long id;
    private String time;
    private String type;
    private String content;

    public OperationLog() {}

    public OperationLog(Long id, String time, String type, String content) {
        this.id = id;
        this.time = time;
        this.type = type;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
