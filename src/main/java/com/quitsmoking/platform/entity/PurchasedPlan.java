package com.quitsmoking.platform.entity;

import com.quitsmoking.platform.enums.PaymentStatus;
import com.quitsmoking.platform.enums.PurchasedTemplateType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import com.quitsmoking.platform.entity.Coach;

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

    private Boolean used = false;

    @OneToOne(mappedBy = "purchasedPlan")
    private QuitPlan linkedQuitPlan;

    private LocalDateTime purchasedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "template_type")
    private PurchasedTemplateType templateType;

    @ManyToOne
    private Coach coach;

    private LocalDate activationDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

}
