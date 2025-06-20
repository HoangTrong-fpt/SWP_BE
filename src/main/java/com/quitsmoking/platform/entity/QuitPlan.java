package com.quitsmoking.platform.entity;

import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PlanStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class QuitPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private LocalDate targetQuitDate;
    private String motivationReason;

    @Enumerated(EnumType.STRING)
    private MethodType method; // TEMPLATE, CUSTOM

    @Enumerated(EnumType.STRING)
    private PlanStatus status; // ACTIVE, COMPLETED, FAILED, CANCELLED

    private LocalDate startDate;
    private String goal;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @OneToOne
    @JoinColumn(name = "initial_condition_id", nullable = false)
    private InitialCondition initialCondition;
}
