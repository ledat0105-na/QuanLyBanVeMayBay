package com.example.quanlybanvemaybay.entity;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "promotions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "discount_percent")
    private Integer discountPercent;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "start_date")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder.Default
    @Column(name = "usage_limit")
    private Integer usageLimit = 0;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}