package com.quitsmoking.platform.dto;

import lombok.Data;

@Data
public class SmokingLogRequest {
    private int cigarettesToday;
    private String note;
}
