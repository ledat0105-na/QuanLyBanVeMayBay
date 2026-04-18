package com.example.quanlybanvemaybay.config;

import com.example.quanlybanvemaybay.entity.Banner;
import com.example.quanlybanvemaybay.repository.BannerRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final BannerRepository bannerRepository;

    public GlobalControllerAdvice(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    @ModelAttribute("requestURI")
    public String getRequestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("pageBanners")
    public Map<String, Banner> getPageBanners() {
        return bannerRepository.findAll().stream()
                .filter(b -> b.getIsActive() != null && b.getIsActive())
                .collect(Collectors.toMap(
                    b -> b.getPosition() != null ? b.getPosition() : "HOME_CAROUSEL", 
                    b -> b, 
                    (existing, replacement) -> existing // Keep the first active one encountered
                ));
    }
}
