package com.quitsmoking.platform.entity;

import com.quitsmoking.platform.enums.AddictionLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitialCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private int cigarettesPerDay;
    private String firstSmokeTime;
    private String quitReason;
    private String intentionSince;
    private int readinessScale;
    private String emotion;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private AddictionLevel addictionLevel;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;
}
