package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.MethodType;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class QuitPlanRequest {
    private Long purchasedPlanId;
    private String planDetail;
    private String motivationReason;
    private LocalDate startDate;
    private LocalDate targetQuitDate;
    private MethodType method;
    private List<String> dailyTips;

    public List<String> getDailyTips() {
        return dailyTips;
    }
    public void setDailyTips(List<String> dailyTips) {
        this.dailyTips = dailyTips;
    }
}