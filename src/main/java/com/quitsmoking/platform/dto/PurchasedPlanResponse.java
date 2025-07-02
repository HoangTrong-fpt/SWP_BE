package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.PurchasedTemplateType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PurchasedPlanResponse {
    private Long id;

    private PurchasedTemplateType templateType;

    private Boolean used;

    private LocalDateTime purchasedAt;

    private LocalDate activationDate;

    private Boolean isActive;

    private Long coachId;
}
