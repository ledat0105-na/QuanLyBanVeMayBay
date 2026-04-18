package com.example.quanlybanvemaybay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "ticket_number", length = 50)
    private String ticketNumber;

    @Column(name = "issued_date")
    private LocalDateTime issuedDate;

    @Column(name = "qr_code", length = 255)
    private String qrCode;

    @Column(name = "email_sent")
    private Boolean emailSent;
}