package com.quitsmoking.platform.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PurchasedPlanRequest {
    @NotNull
    @Schema(example = "TEMPLATE_100K")
    private String templateType;
}
