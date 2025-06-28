package com.quitsmoking.platform.dto;

import java.time.LocalDateTime;

public class FeedbackResponse {
    private String fullName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public FeedbackResponse(String fullName, int rating, String comment, LocalDateTime createdAt) {
        this.fullName = fullName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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
}
