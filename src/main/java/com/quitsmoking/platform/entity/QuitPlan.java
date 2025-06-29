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

    @ManyToOne
    private Account account;

    private Long initialConditionId;

    @Column(columnDefinition = "TEXT")
    private String initialConditionSnapshot;

    private LocalDate startDate;
    private LocalDate targetQuitDate;
    private String goal;
    @Column(columnDefinition = "TEXT")
    private String planDetail; // JSON string daily tasks

    private String motivationReason;

    @Enumerated(EnumType.STRING)
    private MethodType method;

    @Enumerated(EnumType.STRING)
    private PlanStatus status;

    private LocalDate createdAt;

    // Link back to purchased plan used for this quit plan
    @OneToOne
    @JoinColumn(name = "purchased_plan_id")
    private PurchasedPlan purchasedPlan;
}
