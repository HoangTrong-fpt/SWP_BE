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

    private String code; // Mã gói để hiển thị
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Double price;
    private Integer duration; // Số ngày áp dụng cho gói (dùng cho template)
    private Boolean coachSupport; // Gói có hỗ trợ huấn luyện viên không
}