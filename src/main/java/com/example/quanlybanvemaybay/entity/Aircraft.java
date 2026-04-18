package com.example.quanlybanvemaybay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aircraft")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aircraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "airline_id")
    private Airline airline;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "total_seats")
    private Integer totalSeats;
}