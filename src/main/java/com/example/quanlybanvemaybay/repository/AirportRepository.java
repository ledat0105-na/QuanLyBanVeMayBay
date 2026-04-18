package com.example.quanlybanvemaybay.repository;

import com.example.quanlybanvemaybay.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
}
