package com.quitsmoking.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PackageRevenueResponse {
    private Long packageId;
    private String packageName;
    private Double totalAmount;
}
