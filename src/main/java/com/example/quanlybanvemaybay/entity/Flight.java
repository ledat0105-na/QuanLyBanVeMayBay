package com.example.quanlybanvemaybay.entity;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flights")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_number", length = 20)
    private String flightNumber;

    @ManyToOne
    @JoinColumn(name = "airline_id")
    private Airline airline;

    @ManyToOne
    @JoinColumn(name = "departure_airport_id")
    private Airport departureAirport;

    @ManyToOne
    @JoinColumn(name = "arrival_airport_id")
    private Airport arrivalAirport;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "departure_time")
    private LocalDateTime departureTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;

    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Builder.Default
    @Column(name = "is_cancelled")
    private Boolean isCancelled = false;
    
    @Column(name = "total_seats")
    private Integer totalSeats;

    @Column(name = "available_seats")
    private Integer availableSeats;

    @Builder.Default
    @OneToMany(mappedBy = "flight")
    private List<Booking> bookings = new ArrayList<>();
}