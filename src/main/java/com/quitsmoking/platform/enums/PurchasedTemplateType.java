package com.quitsmoking.platform.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Valid template plan types. Prices: 100000=LIGHT, 200000=MEDIUM, 300000=HEAVY, 500000=COACH",
        example = "HEAVY")
public enum PurchasedTemplateType {
    /** Free plan - cannot use template method */
    FREE,
    /** Light intensity template plan */
    LIGHT,
    /** Medium intensity template plan */
    MEDIUM,
    /** Heavy intensity template plan */
    HEAVY,
    /** Template plan with coach involvement */
    COACH
}

