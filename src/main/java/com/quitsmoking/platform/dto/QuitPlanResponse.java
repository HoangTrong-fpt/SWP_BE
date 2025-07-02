package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.enums.PurchasedTemplateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

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

    private Long purchasedPlanId;

    private PurchasedTemplateType templateType;

    private PlanStatus status;

    private LocalDate createdAt;
}