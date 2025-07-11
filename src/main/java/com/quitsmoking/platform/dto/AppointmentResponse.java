package com.quitsmoking.platform.dto;

public class AppointmentResponse {
    private String createdAt;
    private String name;
    private String avatar;
    private Long id;
    private Long coachId;
    private Long customerId;
    private String date;
    private String startTime;
    private String endTime;
    private String status;

    public AppointmentResponse(String createdAt, String name, String avatar, Long id, Long coachId, Long customerId, String date, String startTime, String endTime, String status) {
        this.createdAt = createdAt;
        this.name = name;
        this.avatar = avatar;
        this.id = id;
        this.coachId = coachId;
        this.customerId = customerId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
} 