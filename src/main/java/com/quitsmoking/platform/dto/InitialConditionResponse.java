package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.AddictionLevel;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(example = "10")
    private int cigarettesPerDay;
    @Schema(example = "08:00")
    private String firstSmokeTime;
    @Schema(example = "Stress at work")
    private String reasonForStarting;
    @Schema(example = "Improve health")
    private String quitReason;
    @Schema(example = "2020-01-01")
    private String intentionSince;
    @Schema(example = "7")
    private int readinessScale;
    @Schema(example = "Anxious")
    private String emotion;
    @Schema(example = "18")
    private int startSmokingAge;
    @Schema(example = "2")
    private int pricePerCigarette;
    @Schema(example = "20")
    private int cigarettesPerPack;
    @Schema(example = "false")
    private boolean hasTriedToQuit;
    @Schema(example = "false")
    private boolean hasHealthIssues;
    @Schema(example = "70.5")
    private float weightKg;
    @Schema(example = "2024-10-01")
    private LocalDate desiredQuitDate;
    @Schema(example = "2024-07-01T00:00:00")
    private LocalDateTime createdAt;
    @Schema(example = "LIGHT")
    private AddictionLevel addictionLevel;
}
