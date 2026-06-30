package com.example.quanlybanvemaybay.repository;

import com.example.quanlybanvemaybay.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Long> {
    Optional<Airline> findByCode(String code);
}
