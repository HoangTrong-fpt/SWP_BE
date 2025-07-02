package com.quitsmoking.platform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
@Data
public class FreeQuitPlanRequest {
    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private String goal;

    private String motivationReason;

    private String note;
}
