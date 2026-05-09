package com.example.quanlybanvemaybay.repository;

import com.example.quanlybanvemaybay.entity.Booking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    @Query("SELECT b.flight, COUNT(b) as bookingCount FROM Booking b GROUP BY b.flight ORDER BY bookingCount DESC")
    List<Object[]> findPopularFlights(Pageable pageable);

    @Query("SELECT b.flight, COUNT(b) as bookingCount FROM Booking b WHERE b.bookingDate >= :startDate AND b.bookingDate <= :endDate GROUP BY b.flight ORDER BY bookingCount DESC")
    List<Object[]> findPopularFlightsBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    long countByBookingDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Booking> findByUserUsernameOrderByBookingDateDesc(String username);
}
