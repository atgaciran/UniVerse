package com.example.universe.ui.chat;

public class Message {
    private String userId;
    private String userName;
    private String userSurname;
    private String text;
    private long timestamp;

    public Message() {
        // Firebase için boş constructor gerekli
    }

    public Message(String userId, String userName, String userSurname, String text, long timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.userSurname = userSurname;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Getter ve Setter metodları
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}