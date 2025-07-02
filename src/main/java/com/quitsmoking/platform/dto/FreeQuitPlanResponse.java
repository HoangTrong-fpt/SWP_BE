package com.quitsmoking.platform.dto;

import lombok.Data;

import java.time.LocalDate;
@Data
public class FreeQuitPlanResponse {
    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    private String goal;

    private String motivationReason;

    private String note;

    private Boolean active;
}