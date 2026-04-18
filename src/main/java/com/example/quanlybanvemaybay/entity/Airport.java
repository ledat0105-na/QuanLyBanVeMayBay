package com.example.quanlybanvemaybay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "airports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 10)
    private String code;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "country", length = 100)
    private String country;

    @Builder.Default
    @OneToMany(mappedBy = "departureAirport")
    private List<Flight> departureFlights = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "arrivalAirport")
    private List<Flight> arrivalFlights = new ArrayList<>();
}