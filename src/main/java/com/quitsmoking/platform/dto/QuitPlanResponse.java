package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PlanStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class QuitPlanResponse {
    private Long id;
    private LocalDate targetQuitDate;
    private String motivationReason;
    private MethodType method;
    private PlanStatus status;
    private LocalDate startDate;
    private String goal;
}
