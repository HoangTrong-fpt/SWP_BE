package com.quitsmoking.platform.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackageRequest {
    private String code;
    private String name;
    private String description;
    private Double price;
    private Integer duration;
    private Boolean coachSupport;
}