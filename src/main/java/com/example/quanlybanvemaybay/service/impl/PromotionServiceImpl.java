package com.example.quanlybanvemaybay.service.impl;

import com.example.quanlybanvemaybay.entity.Promotion;
import com.example.quanlybanvemaybay.repository.PromotionRepository;
import com.example.quanlybanvemaybay.service.itf.PromotionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionServiceImpl(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    public List<Promotion> findAll() {
        return promotionRepository.findAll();
    }

    @Override
    public Promotion findById(Long id) {
        return promotionRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Promotion promotion) {
        promotionRepository.save(promotion);
    }

    @Override
    public void toggleStatus(Long id) {
        Promotion promotion = findById(id);
        if (promotion != null) {
            promotion.setIsActive(promotion.getIsActive() == null ? false : !promotion.getIsActive());
            promotionRepository.save(promotion);
        }
    }
}
