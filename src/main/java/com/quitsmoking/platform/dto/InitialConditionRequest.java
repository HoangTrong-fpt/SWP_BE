package com.quitsmoking.platform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class InitialConditionRequest {
    private int cigarettesPerDay;
    private String firstSmokeTime;
    private String reasonForStarting;
    private String quitReason;
    @NotNull
    private LocalDate intentionSince;
    private int readinessScale;
    private String emotion;
    private int startSmokingAge;
    private int pricePerCigarette;
    private int cigarettesPerPack;
    private boolean hasTriedToQuit;
    private boolean hasHealthIssues;
    private float weightKg;
    private LocalDate desiredQuitDate;
}
