package com.example.quanlybanvemaybay.repository;

import com.example.quanlybanvemaybay.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentStatus = 'SUCCESS' OR p.paymentStatus = 'CONFIRMED'")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT SUM(p.amount) FROM Payment p JOIN p.booking b WHERE (p.paymentStatus = 'SUCCESS' OR p.paymentStatus = 'CONFIRMED') AND b.bookingDate >= :startDate AND b.bookingDate <= :endDate")
    BigDecimal calculateTotalRevenueBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
