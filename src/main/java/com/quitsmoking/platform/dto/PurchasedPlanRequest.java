package com.quitsmoking.platform.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PurchasedPlanRequest {
    @NotNull
    @Schema(example = "MEDIUM",
            allowableValues = {"FREE", "LIGHT", "MEDIUM", "HEAVY", "COACH"})
    private String templateType;
}
