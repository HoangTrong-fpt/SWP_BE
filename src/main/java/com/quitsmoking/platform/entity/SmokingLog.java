package com.quitsmoking.platform.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SmokingLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account account;

    @ManyToOne
    private QuitPlan quitPlan;

    @ManyToOne
    private FreeQuitPlan freeQuitPlan;

    private LocalDate date;
    private Integer cigarettesToday;
    private Integer price;
    private String note;
}
