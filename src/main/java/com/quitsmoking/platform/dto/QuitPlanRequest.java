package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.MethodType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Data
public class QuitPlanRequest {
    private Long purchasedPlanId;

    private LocalDate startDate;

    private LocalDate targetQuitDate;

    private String goal;

    private String planDetail;

    private String motivationReason;

    private MethodType method;
}
