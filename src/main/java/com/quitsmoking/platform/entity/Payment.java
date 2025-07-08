package com.quitsmoking.platform.entity;

import com.quitsmoking.platform.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;
    @Column(length = 2000)
    private String paymentUrl;
    private Double amount;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    // ĐÂY: dùng JPA ManyToOne, không còn Long purchasedPlanId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchased_plan_id")
    private PurchasedPlan purchasedPlan;



}
