package com.quitsmoking.platform.dto;

import java.time.LocalDateTime;

public class FeedbackResponse {
    private Long id;
    private String comment;
    private int rating;
    private Long blogId;
    private Long accountId;
    private User user;
    private LocalDateTime createdAt;

    public static class User {
        private Long id;
        private String username;
        private String fullName;
        private String avatarUrl;

        public User(Long id, String username, String fullName, String avatarUrl) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
            this.avatarUrl = avatarUrl;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    }

    public FeedbackResponse(Long id, String comment, int rating, Long blogId, Long accountId, User user, LocalDateTime createdAt) {
        this.id = id;
        this.comment = comment;
        this.rating = rating;
        this.blogId = blogId;
        this.accountId = accountId;
        this.user = user;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public Long getBlogId() { return blogId; }
    public void setBlogId(Long blogId) { this.blogId = blogId; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}