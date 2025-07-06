package com.quitsmoking.platform.dto;

import lombok.Data;

@Data
public class PurchasedPlanRequest {
    private String packageCode;
    private Long coachId; // nullable nếu gói không cần coach
}

