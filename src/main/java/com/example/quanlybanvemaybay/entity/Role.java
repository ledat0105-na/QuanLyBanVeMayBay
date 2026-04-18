package com.example.quanlybanvemaybay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role_name", length = 20)
    private String roleName;

    @OneToMany(mappedBy = "role")
    private List<User> users = new ArrayList<>();
}