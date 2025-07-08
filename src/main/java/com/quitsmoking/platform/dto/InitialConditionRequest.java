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

    private int readinessScale;

    private String emotion;

    private int pricePerCigarette;

    private int cigarettesPerPack;

    private boolean hasTriedToQuit;

    private boolean hasHealthIssues;

    private float weightKg;

}