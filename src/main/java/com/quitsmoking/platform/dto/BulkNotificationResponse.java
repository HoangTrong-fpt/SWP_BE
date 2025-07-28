package com.quitsmoking.platform.dto;

import java.time.LocalDateTime;

public class BulkNotificationResponse {
    private String message;
    private int sentCount;
    private LocalDateTime sentAt;
    private String title;
    private String type;

    public BulkNotificationResponse(String message, int sentCount, LocalDateTime sentAt, String title, String type) {
        this.message = message;
        this.sentCount = sentCount;
        this.sentAt = sentAt;
        this.title = title;
        this.type = type;
    }

    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getSentCount() { return sentCount; }
    public void setSentCount(int sentCount) { this.sentCount = sentCount; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}