package com.example.quanlybanvemaybay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "airlines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "code", length = 10)
    private String code;

    @Builder.Default
    @OneToMany(mappedBy = "airline")
    private List<Aircraft> aircraftList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "airline")
    private List<Flight> flights = new ArrayList<>();
}