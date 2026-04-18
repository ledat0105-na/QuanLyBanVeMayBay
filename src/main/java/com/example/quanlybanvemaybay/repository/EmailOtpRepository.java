package com.example.quanlybanvemaybay.repository;

import com.example.quanlybanvemaybay.entity.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {
    Optional<EmailOtp> findFirstByEmailAndOtpCodeAndIsUsedFalseOrderByExpireTimeDesc(String email, String otpCode);
}

