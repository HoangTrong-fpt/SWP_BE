package com.quitsmoking.platform.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TotalSuccessAmountResponse {
    private Double totalSuccessAmount;
    private String currency;
}
