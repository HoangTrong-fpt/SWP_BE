package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.AddictionLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InitialConditionResponse {
    private int cigarettesPerDay;
    private String firstSmokeTime;
    private String quitReason;
    private String intentionSince;
    private int readinessScale;
    private String emotion;
    private LocalDateTime createdAt;
    private AddictionLevel addictionLevel;
}
