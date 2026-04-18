package com.example.quanlybanvemaybay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "subject", length = 255)
    private String subject;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}