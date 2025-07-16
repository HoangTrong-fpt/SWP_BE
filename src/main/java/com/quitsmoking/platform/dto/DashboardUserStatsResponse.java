package com.quitsmoking.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardUserStatsResponse {
    private long totalUser;
    private long activeUser;
    private long userHasPurchasedPlan;
}
