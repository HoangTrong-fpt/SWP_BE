package com.quitsmoking.platform.entity;

import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PlanStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class QuitPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Account account;
    @OneToOne
    private PurchasedPlan purchasedPlan;
    private LocalDate startDate;
    private LocalDate targetQuitDate;
    @Column(columnDefinition = "TEXT")
    private String planDetail;
    private String motivationReason;
    @Column(columnDefinition = "TEXT")
    private String initialConditionSnapshot;
    private Long initialConditionId;
    private MethodType method;
    private PlanStatus status;
    private LocalDate createdAt;
}