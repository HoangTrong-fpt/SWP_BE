package com.quitsmoking.platform.entity;

import com.quitsmoking.platform.enums.AddictionLevel;
import com.quitsmoking.platform.enums.InitialConditionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitialCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account account;

    private int cigarettesPerDay;
    private String firstSmokeTime;
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
    private LocalDate desiredQuitDate;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private AddictionLevel addictionLevel;

    @Enumerated(EnumType.STRING)
    private InitialConditionType type;

    private int version;

    private boolean isActive;
}
