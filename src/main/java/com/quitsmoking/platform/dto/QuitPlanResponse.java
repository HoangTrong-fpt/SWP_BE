package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.enums.PurchasedTemplateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
public class QuitPlanResponse {
    @Schema(example = "1")
    private Long id;
    @Schema(example = "10")
    private Long initialConditionId;
    @Schema(example = "{\"cigarettesPerDay\":10}")
    private String initialConditionSnapshot;
    @Schema(example = "2024-08-01")
    private LocalDate startDate;
    @Schema(example = "2024-09-01")
    private LocalDate targetQuitDate;
    @Schema(example = "Quit smoking in 30 days")
    private String goal;
    @Schema(example = "Generated plan detail")
    private String planDetail;
    @Schema(example = "Stay healthy for family")
    private String motivationReason;
    @Schema(example = "TEMPLATE")
    private MethodType method;
    @Schema(example = "1")
    private Long purchasedPlanId;
    @Schema(example = "HEAVY")
    private PurchasedTemplateType templateType;
    @Schema(example = "ACTIVE")
    private PlanStatus status;
    @Schema(example = "2024-07-01")
    private LocalDate createdAt;
}
