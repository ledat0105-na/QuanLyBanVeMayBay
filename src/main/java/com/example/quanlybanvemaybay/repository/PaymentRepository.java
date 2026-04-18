package com.example.quanlybanvemaybay.repository;

import com.example.quanlybanvemaybay.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentStatus = 'SUCCESS' OR p.paymentStatus = 'CONFIRMED'")
    BigDecimal calculateTotalRevenue();
}
