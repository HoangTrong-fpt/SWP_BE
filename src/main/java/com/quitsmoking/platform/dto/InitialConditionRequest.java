package com.quitsmoking.platform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitialConditionRequest {
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
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Ngày phải đúng định dạng yyyy-MM-dd")
    @Schema(example = "2024-10-01")
    private String desiredQuitDate;
}
