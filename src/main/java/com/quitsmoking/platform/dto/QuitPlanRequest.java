package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.MethodType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class QuitPlanRequest {
    @Schema(example = "2024-08-01")
    private LocalDate startDate;
    @Schema(example = "2024-09-01")
    private LocalDate targetQuitDate;
    @Schema(example = "Quit smoking in 30 days")
    private String goal;
    @Schema(example = "Custom plan details") // FE gửi nếu CUSTOM, BE sinh nếu TEMPLATE
    private String planDetail; // FE gửi nếu CUSTOM, BE sinh nếu TEMPLATE
    @Schema(example = "Stay healthy for family")
    private String motivationReason;
    @Schema(example = "TEMPLATE") // TEMPLATE hoặc CUSTOM
    private MethodType method; // TEMPLATE hoặc CUSTOM
    @Schema(example = "LIGHT") // LIGHT/MEDIUM/HEAVY/COACH, dùng cho TEMPLATE
    private String templateType; // LIGHT/MEDIUM/HEAVY/COACH, dùng cho TEMPLATE
    @Schema(example = "1")
    private Long purchasedPlanId;
}
