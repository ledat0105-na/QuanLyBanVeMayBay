package com.example.quanlybanvemaybay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "flight_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightServiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon_class", length = 50)
    private String iconClass; // e.g., "fas fa-utensils"

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}
