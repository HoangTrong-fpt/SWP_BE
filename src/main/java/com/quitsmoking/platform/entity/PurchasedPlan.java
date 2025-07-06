package com.quitsmoking.platform.entity;

import com.quitsmoking.platform.enums.PaymentStatus;
import com.quitsmoking.platform.enums.PlanStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PurchasedPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account account;

    @ManyToOne
    private Package planPackage;

    @ManyToOne
    private Coach coach;

    private LocalDateTime purchasedAt;
    private LocalDate activationDate;
    private PaymentStatus paymentStatus;
    private PlanStatus status;
    private Boolean used;

    @OneToOne
    private QuitPlan linkedQuitPlan;


}
