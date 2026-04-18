package com.example.quanlybanvemaybay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "baggage_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaggageOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;
}