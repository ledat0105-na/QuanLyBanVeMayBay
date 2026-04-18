package com.example.quanlybanvemaybay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "is_locked")
    @Transient // Temporarily ignore this field from DB to fix Unknown column error
    private Boolean isLocked = false;

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<EmailLog> emailLogs = new ArrayList<>();
}