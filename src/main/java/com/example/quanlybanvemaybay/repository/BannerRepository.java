package com.example.quanlybanvemaybay.repository;

import com.example.quanlybanvemaybay.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByIsActiveTrue();
}
