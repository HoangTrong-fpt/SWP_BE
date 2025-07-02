package com.quitsmoking.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FreeQuitPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account account;

    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active = true;

    private String goal;
    private String motivationReason;
    private String note;
}