package com.example.quanlybanvemaybay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_otp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "otp_code", length = 10)
    private String otpCode;

    @Column(name = "expire_time")
    private LocalDateTime expireTime;

    @Column(name = "is_used")
    private Boolean isUsed;
}