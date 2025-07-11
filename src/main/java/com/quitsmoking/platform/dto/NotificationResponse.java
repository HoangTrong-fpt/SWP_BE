package com.quitsmoking.platform.dto;

import java.time.ZonedDateTime;

public class NotificationResponse {
    private String email;
    private String subject;
    private String content;
    private ZonedDateTime sendAt;
    private boolean sent;

    public NotificationResponse(String email, String subject, String content, ZonedDateTime sendAt, boolean sent) {
        this.email = email;
        this.subject = subject;
        this.content = content;
        this.sendAt = sendAt;
        this.sent = sent;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getSendAt() {
        return sendAt;
    }

    public void setSendAt(ZonedDateTime sendAt) {
        this.sendAt = sendAt;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
