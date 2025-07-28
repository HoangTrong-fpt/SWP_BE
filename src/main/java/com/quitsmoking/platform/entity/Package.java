package com.quitsmoking.platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Double price;
    private Integer duration;
    private Boolean coachSupport;
}