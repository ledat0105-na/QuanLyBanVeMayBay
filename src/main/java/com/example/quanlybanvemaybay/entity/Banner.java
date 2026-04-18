package com.example.quanlybanvemaybay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "banners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 200)
    private String title;
    
    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "position", length = 50)
    private String position;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}
