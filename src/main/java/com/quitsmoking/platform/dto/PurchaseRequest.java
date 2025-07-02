package com.quitsmoking.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseRequest {
    @Schema(description = "Số tiền gói (100000=LIGHT, 200000=MEDIUM, 300000=HEAVY, 500000=COACH)",
            example = "100000")
    private int amount;
}
