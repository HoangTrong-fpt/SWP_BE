package com.quitsmoking.platform.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PurchasedPlanRequest {
    @NotNull
    @Schema(example = "100000", description = "Số tiền gói (100000=LIGHT, 200000=MEDIUM, 300000=HEAVY, 500000=COACH)")
    private Integer amount;
}
