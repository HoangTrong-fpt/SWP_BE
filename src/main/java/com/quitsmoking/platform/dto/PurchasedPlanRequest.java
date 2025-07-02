package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.PurchasedTemplateType;
import lombok.Data;

@Data
public class PurchasedPlanRequest {
    private PurchasedTemplateType templateType;

    //private Long coachId; // Optional
}
