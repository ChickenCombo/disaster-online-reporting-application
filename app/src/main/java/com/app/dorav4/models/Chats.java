package com.app.dorav4.models;

public class Chats {
    private String chatId, message, userId;

    public Chats() {
    }

    public Chats(String chatId, String message, String userId) {
        this.chatId = chatId;
        this.message = message;
        this.userId = userId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
