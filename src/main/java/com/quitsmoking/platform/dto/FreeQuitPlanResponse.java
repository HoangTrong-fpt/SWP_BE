package com.quitsmoking.platform.dto;

import lombok.Data;

import java.time.LocalDate;
@Data
public class FreeQuitPlanResponse {
    private Long id;
    private Long accountId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;
    private String motivationReason;
}