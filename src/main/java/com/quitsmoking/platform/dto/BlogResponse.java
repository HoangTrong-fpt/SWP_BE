package com.quitsmoking.platform.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BlogResponse {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
    private List<FeedbackResponse> feedbacks;

    public BlogResponse(Long id, String title, String content, String imageUrl, LocalDateTime createdAt, List<FeedbackResponse> feedbacks) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.feedbacks = feedbacks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<FeedbackResponse> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<FeedbackResponse> feedbacks) {
        this.feedbacks = feedbacks;
    }
}
