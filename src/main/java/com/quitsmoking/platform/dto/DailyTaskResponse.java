package com.quitsmoking.platform.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DailyTaskResponse {
    private LocalDate date;
    private Integer targetSmokePerDay;
    private String note;
    private Boolean completed;
    private String userNote;
}