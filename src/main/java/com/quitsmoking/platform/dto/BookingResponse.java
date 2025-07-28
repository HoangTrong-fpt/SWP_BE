package com.quitsmoking.platform.dto;

import lombok.Data;

@Data
public class BookingResponse {
    private Long bookingId;
    private Long coachId;
    private String coachName;
    private String date;
    private String startTime;
    private String endTime;
    private String status;
    private String createdAt;
    private String updatedAt;
    private UserInfo user;
    private String coachAvatar;

    @Data
    public static class UserInfo {
        private Long customerId;
        private String fullName;
        private String email;
        private String phoneNumber;
        private String avatarUrl;
    }
}
