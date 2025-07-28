package com.quitsmoking.platform.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DailyTaskRequest {
    private LocalDate date;
    private Integer targetSmokePerDay;
    private String note;
}

