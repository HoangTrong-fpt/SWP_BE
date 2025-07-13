package com.quitsmoking.platform.dto;

import lombok.Data;

@Data
public class SmokingLogRequest {
    private int cigarettesToday; // Số điếu thực tế hút
    private String note;         // Ghi chú tự do
}
