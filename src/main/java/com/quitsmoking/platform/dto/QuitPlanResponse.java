package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PlanStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class QuitPlanResponse {
    private Long id;
    private Long initialConditionId;
    private LocalDate startDate;
    private LocalDate targetQuitDate;
    private String goal;
    private String planDetail;
    private String motivationReason;
    private MethodType method;
    private Long purchasedPlanId;
    private PlanStatus status;
    private LocalDate createdAt;
}
