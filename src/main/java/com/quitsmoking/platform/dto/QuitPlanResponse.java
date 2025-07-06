package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PlanStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class QuitPlanResponse {
    private Long id;
    private Long initialConditionId;
    private String initialConditionSnapshot;
    private LocalDate startDate;
    private LocalDate targetQuitDate;
    private String goal;
    private String planDetail;
    private String motivationReason;
    private MethodType method;
    private PlanStatus status;
    private LocalDate createdAt;
    private Long purchasedPlanId;
    private PackageResponse packageInfo;
}