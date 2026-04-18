package com.example.quanlybanvemaybay.service.itf;

import com.example.quanlybanvemaybay.entity.Promotion;

import java.util.List;

public interface PromotionService {
    List<Promotion> findAll();
    Promotion findById(Long id);
    void save(Promotion promotion);
    void toggleStatus(Long id);
}
