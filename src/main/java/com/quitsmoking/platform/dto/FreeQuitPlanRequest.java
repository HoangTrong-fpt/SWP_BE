package com.quitsmoking.platform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
@Data
public class FreeQuitPlanRequest {
    private Long accountId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String motivationReason;
}
