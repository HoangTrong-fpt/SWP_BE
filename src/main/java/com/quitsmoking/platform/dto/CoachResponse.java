package com.quitsmoking.platform.dto;

import lombok.Data;

@Data
public class CoachResponse {
    private Long id;
    private Long accountId;
    private String fullName;
    private String phone;
    private String email;
    private String avatarUrl;
    private String description;
}
