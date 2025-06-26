package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.MethodType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class QuitPlanRequest {
    private LocalDate startDate;
    private LocalDate targetQuitDate;
    private String goal;
    private String planDetail; // FE gửi nếu CUSTOM, BE sinh nếu TEMPLATE
    private String motivationReason;
    private MethodType method; // TEMPLATE hoặc CUSTOM
    private String templateType; // LIGHT/MEDIUM/HEAVY/COACH, dùng cho TEMPLATE
}
