package com.example.quanlybanvemaybay.repository;

import com.example.quanlybanvemaybay.entity.BaggageOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaggageOptionRepository extends JpaRepository<BaggageOption, Long> {
}
