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

    private String quitReason;

    private String emotion;

    private int pricePerCigarette;

    private boolean hasTriedToQuit;

    private boolean hasHealthIssues;

}