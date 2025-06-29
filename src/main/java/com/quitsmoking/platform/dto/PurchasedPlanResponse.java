package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.PurchasedTemplateType;
import lombok.Data;

@Data
public class PurchasedPlanResponse {
    private Long id;
    private PurchasedTemplateType templateType;
}
