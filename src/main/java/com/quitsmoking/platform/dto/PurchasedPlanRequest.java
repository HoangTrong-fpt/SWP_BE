package com.quitsmoking.platform.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PurchasedPlanRequest {
    @NotNull
    @Schema(example = "TEMPLATE_100K",
            allowableValues = {"FREE", "TEMPLATE_100K", "TEMPLATE_200K",
                    "TEMPLATE_300K", "TEMPLATE_500K"})
    private String templateType;
}
