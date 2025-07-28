// Entity: DailyTask.java
package com.quitsmoking.platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class DailyTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchased_plan_id")
    private PurchasedPlan purchasedPlan;


    private LocalDate date;
    private Integer targetSmokePerDay;
    private String note;

    private Boolean completed = false; // user đánh dấu hoàn thành
    private String userNote; // phản hồi của user (nếu có)
}