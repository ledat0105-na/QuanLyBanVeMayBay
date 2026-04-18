package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.entity.Banner;
import com.example.quanlybanvemaybay.repository.BannerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin/visual-editor")
public class AdminVisualEditorController {

    private final BannerRepository bannerRepository;
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/banners/";

    public AdminVisualEditorController(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "File is empty"));
            }
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "url", "/uploads/banners/" + fileName
            ));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Upload failed: " + e.getMessage()));
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveConfiguration(@RequestParam("position") String position,
                                               @RequestParam("imageUrl") String imageUrl) {
        // Find existing configuration banner or create a new one
        Optional<Banner> existing = bannerRepository.findAll().stream()
                .filter(b -> position.equals(b.getPosition()))
                .findFirst();

        Banner banner = existing.orElse(new Banner());
        banner.setPosition(position);
        banner.setImageUrl(imageUrl);
        banner.setTitle("Visual Configuration");
        banner.setIsActive(true);

        bannerRepository.save(banner);

        return ResponseEntity.ok(Map.of("success", true, "message", "Saved successfully"));
    }
}
