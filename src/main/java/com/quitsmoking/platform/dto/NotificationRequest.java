package com.quitsmoking.platform.dto;

import java.util.List;

public class NotificationRequest {
    private List<Long> recipientIds;
    private Long senderId; // Có thể null
    private String title;
    private String message;
    private String type;
    // getters/setters
    public List<Long> getRecipientIds() { return recipientIds; }
    public void setRecipientIds(List<Long> recipientIds) { this.recipientIds = recipientIds; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}

