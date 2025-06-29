package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.PurchasedTemplateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PurchasedPlanResponse {
    @Schema(example = "1")
    private Long id;
    @Schema(example = "FREE")
    private PurchasedTemplateType templateType;
}
