package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.entity.Banner;
import com.example.quanlybanvemaybay.repository.BannerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/admin/banners")
public class AdminBannerController {

    private final BannerRepository bannerRepository;
    
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/banners/";

    public AdminBannerController(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    @GetMapping
    public String listBanners(Model model) {
        model.addAttribute("banners", bannerRepository.findAll());
        return "admin/manage-banners";
    }

    @PostMapping("/save")
    public String saveBanner(@RequestParam(value = "id", required = false) Long id,
                             @RequestParam("title") String title,
                             @RequestParam("subtitle") String subtitle,
                             @RequestParam(value = "position", defaultValue = "HOME_CAROUSEL") String position,
                             @RequestParam(value = "isActive", defaultValue = "false") Boolean isActive,
                             @RequestParam(value = "imageUrlStr", required = false) String imageUrlStr,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
                             
        Banner banner;
        if (id != null) {
            banner = bannerRepository.findById(id).orElse(new Banner());
        } else {
            banner = new Banner();
        }
        
        banner.setTitle(title);
        banner.setSubtitle(subtitle);
        banner.setPosition(position);
        banner.setIsActive(isActive);
        
        if (imageUrlStr != null && !imageUrlStr.trim().isEmpty()) {
            banner.setImageUrl(imageUrlStr.trim());
        } else if (imageFile != null && !imageFile.isEmpty()) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath);
            
            banner.setImageUrl("/uploads/banners/" + fileName);
        }
        
        bannerRepository.save(banner);
        return "redirect:/admin/banners";
    }

    @PostMapping("/delete/{id}")
    public String deleteBanner(@PathVariable Long id) {
        bannerRepository.deleteById(id);
        return "redirect:/admin/banners";
    }
}
