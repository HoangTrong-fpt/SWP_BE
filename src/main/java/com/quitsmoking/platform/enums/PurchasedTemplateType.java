package com.quitsmoking.platform.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Valid template plan types", example = "TEMPLATE_100K")
public enum PurchasedTemplateType {
    FREE,
    TEMPLATE_100K,
    TEMPLATE_200K,
    TEMPLATE_300K,
    TEMPLATE_500K
}

