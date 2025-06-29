package com.quitsmoking.platform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchasedPlanRequest {
    @NotNull
    private String templateType;
}
