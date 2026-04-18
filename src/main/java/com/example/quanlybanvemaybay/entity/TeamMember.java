package com.example.quanlybanvemaybay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "role", length = 100)
    private String role; // e.g., "Cơ trưởng" or "Tiếp viên trưởng"

    @Column(name = "image_url")
    private String imageUrl;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}
