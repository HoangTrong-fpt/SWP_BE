package com.quitsmoking.platform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitialConditionRequest {
    private int cigarettesPerDay;
    private String firstSmokeTime;
    private String reasonForStarting;
    private String quitReason;
    private String intentionSince;
    private int readinessScale;
    private String emotion;
    private int startSmokingAge;
    private int pricePerCigarette;
    private int cigarettesPerPack;
    private boolean hasTriedToQuit;
    private boolean hasHealthIssues;
    private float weightKg;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Ngày phải đúng định dạng yyyy-MM-dd")
    private String desiredQuitDate;
}
