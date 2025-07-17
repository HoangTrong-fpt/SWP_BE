package com.quitsmoking.platform.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailySuccessAmountResponse {
    private String currency;
    private Double total;
    private List<DailyAmount> data;

    @Getter
    @AllArgsConstructor
    public static class DailyAmount {
        private String date;
        private Double amount;
    }
}
