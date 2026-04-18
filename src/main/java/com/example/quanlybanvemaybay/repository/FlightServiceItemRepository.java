package com.example.quanlybanvemaybay.repository;

import com.example.quanlybanvemaybay.entity.FlightServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightServiceItemRepository extends JpaRepository<FlightServiceItem, Long> {
    List<FlightServiceItem> findByIsActiveTrue();
}
