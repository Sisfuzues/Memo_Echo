package com.memoecho.persistence.pojo.DTO;
public class SensitiveRecord {
    private Long id;
    private String time;
    private String groupName;
    private String word;
    private String content;

    public SensitiveRecord() {}

    public SensitiveRecord(Long id, String time, String groupName, String word, String content) {
        this.id = id;
        this.time = time;
        this.groupName = groupName;
        this.word = word;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getWord() {
        return word;
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

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setContent(String content) {
        this.content = content;
    }
}