package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.AddictionLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InitialConditionResponse {
    private int cigarettesPerDay;
    private String firstSmokeTime;
    private String reasonForStarting;
    private String quitReason;
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
    private LocalDateTime createdAt;
    private AddictionLevel addictionLevel;
}
