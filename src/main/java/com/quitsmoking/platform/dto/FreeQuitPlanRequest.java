package com.quitsmoking.platform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
@Data
public class FreeQuitPlanRequest {
    private Long accountId;           // id tài khoản user gửi lên
    private LocalDate startDate;
    private LocalDate endDate;
    private String motivationReason;
    private String note;


}
