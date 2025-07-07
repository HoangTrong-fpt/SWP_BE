package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.entity.InitialCondition;
import com.quitsmoking.platform.enums.AddictionLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

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

    private int readinessScale;

    private String emotion;

    private int pricePerCigarette;

    private int cigarettesPerPack;

    private boolean hasTriedToQuit;

    private boolean hasHealthIssues;

    private float weightKg;

    private LocalDateTime createdAt;

    private AddictionLevel addictionLevel;

    private String addictionLevelLabel;

}