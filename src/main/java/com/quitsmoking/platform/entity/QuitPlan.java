package com.quitsmoking.platform.entity;

import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.enums.PurchasedTemplateType;
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

    private Long initialConditionId;

    private String initialConditionSnapshot;

    private LocalDate startDate;

    private LocalDate targetQuitDate;

    private String goal;

    @Column(columnDefinition = "TEXT")
    private String planDetail;

    private String motivationReason;

    @Enumerated(EnumType.STRING)
    private MethodType method;

    @Enumerated(EnumType.STRING)
    private PurchasedTemplateType templateType;

    @Enumerated(EnumType.STRING)
    private PlanStatus status;

    private LocalDate createdAt;

    @OneToOne
    private PurchasedPlan purchasedPlan;
}