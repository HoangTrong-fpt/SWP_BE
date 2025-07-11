package com.quitsmoking.platform.dto;

import lombok.Data;

@Data
public class PaymentConfirmRequest {
    private Long paymentId;
    private Long planId;
    private String paymentStatus; // "SUCCESS" hoặc "FAILED"
}