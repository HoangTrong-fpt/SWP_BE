package com.quitsmoking.platform.dto;

import java.time.LocalDateTime;

public class FeedbackResponse {
    private Long id;
    private Long accountId;
    private String username;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public FeedbackResponse(Long id, Long accountId, String username, int rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.username = username;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
