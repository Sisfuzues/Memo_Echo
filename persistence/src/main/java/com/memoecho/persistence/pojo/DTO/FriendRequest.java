package com.memoecho.persistence.pojo.DTO;

public class FriendRequest {
    private Long id;
    private String nickname;
    private String userId;
    private String reason;
    private String status;

    public FriendRequest() {}

    public FriendRequest(Long id, String nickname, String userId, String reason, String status) {
        this.id = id;
        this.nickname = nickname;
        this.userId = userId;
        this.reason = reason;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUserId() {
        return userId;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}