package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.MethodType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class QuitPlanRequest {
    private LocalDate targetQuitDate;
    private String motivationReason;
    private MethodType method; // TEMPLATE hoặc CUSTOM
    private LocalDate startDate;
    private String goal;
    private Long initialConditionId; // dùng để ràng buộc premium không cập nhật lung tung
}
