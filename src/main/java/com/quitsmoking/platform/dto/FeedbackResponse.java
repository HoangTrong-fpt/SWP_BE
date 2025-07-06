package com.quitsmoking.platform.dto;

public class FeedbackResponse {
    private Long id;
    private String comment;
    private int rating;
    private Long blogId;
    private Long accountId;
    private UserInfo user;

    public static class UserInfo {
        private Long id;
        private String username;
        private String fullName;
        private String avatarUrl;

        public UserInfo(Long id, String username, String fullName, String avatarUrl) {
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

    public FeedbackResponse(Long id, String comment, int rating, Long blogId, Long accountId, UserInfo user) {
        this.id = id;
        this.comment = comment;
        this.rating = rating;
        this.blogId = blogId;
        this.accountId = accountId;
        this.user = user;
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
    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }
}
