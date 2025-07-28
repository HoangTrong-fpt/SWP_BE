package com.quitsmoking.platform.dto;

import lombok.Data;

@Data
public class SmokingStatsSummary {
    private int totalDays;
    private int totalCigarettes;
    private int totalMoneySaved;
    private int daysAchievedTarget;
    private boolean isFreePlan;
}