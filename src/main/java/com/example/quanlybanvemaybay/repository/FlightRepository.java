package com.example.quanlybanvemaybay.repository;

import com.example.quanlybanvemaybay.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Query("SELECT f FROM Flight f WHERE f.departureAirport.id = :depId AND f.arrivalAirport.id = :arrId AND DATE(f.departureTime) = :depDate AND f.isCancelled = false AND (f.availableSeats IS NULL OR f.availableSeats >= :passengers) ORDER BY f.basePrice ASC")
    List<Flight> searchFlights(@Param("depId") Long depId, @Param("arrId") Long arrId, @Param("depDate") LocalDate depDate, @Param("passengers") int passengers);
}
