package com.example.quanlybanvemaybay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_code", length = 20)
    private String bookingCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;

    @ManyToOne
    @JoinColumn(name = "return_flight_id")
    private Flight returnFlight;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "booking_status", length = 50)
    private String bookingStatus;

    @Column(name = "booking_date")
    private LocalDateTime bookingDate;

    @Builder.Default
    @OneToMany(mappedBy = "booking")
    private List<Passenger> passengers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "booking")
    private List<Payment> payments = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
        name = "booking_baggage",
        joinColumns = @JoinColumn(name = "booking_id"),
        inverseJoinColumns = @JoinColumn(name = "baggage_id")
    )
    private List<BaggageOption> baggageOptions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "booking")
    private List<Ticket> tickets = new ArrayList<>();

    @Builder.Default
    @Column(name = "is_checked_in")
    private Boolean isCheckedIn = false;

    public boolean isCheckedIn() {
        return isCheckedIn != null && isCheckedIn;
    }

    public boolean isCheckInAllowed() {
        if (!"CONFIRMED".equals(bookingStatus) || flight == null || flight.getDepartureTime() == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime openTime = flight.getDepartureTime().minusHours(23);
        LocalDateTime closeTime = flight.getDepartureTime().minusHours(1);
        return now.isAfter(openTime) && now.isBefore(closeTime);
    }

    public boolean isCheckInClosed() {
        if (flight == null || flight.getDepartureTime() == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime closeTime = flight.getDepartureTime().minusHours(1);
        return now.isAfter(closeTime);
    }

    public boolean isCheckInNotOpenYet() {
        if (flight == null || flight.getDepartureTime() == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime openTime = flight.getDepartureTime().minusHours(23);
        return now.isBefore(openTime);
    }
}