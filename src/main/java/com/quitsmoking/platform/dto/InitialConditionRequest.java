package com.quitsmoking.platform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitialConditionRequest {
    @Min(0)
    private int cigarettesPerDay;

    @NotBlank
    private String firstSmokeTime;

    private String quitReason;
    private String intentionSince;

    @Min(1) @Max(10)
    private int readinessScale;

    private String emotion;
}
