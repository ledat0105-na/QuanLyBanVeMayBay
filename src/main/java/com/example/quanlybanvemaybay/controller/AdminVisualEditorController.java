package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.entity.Banner;
import com.example.quanlybanvemaybay.entity.FlightServiceItem;
import com.example.quanlybanvemaybay.entity.TeamMember;
import com.example.quanlybanvemaybay.repository.BannerRepository;
import com.example.quanlybanvemaybay.repository.FlightServiceItemRepository;
import com.example.quanlybanvemaybay.repository.TeamMemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/admin/visual-editor")
public class AdminVisualEditorController {

    private final BannerRepository bannerRepository;
    private final com.example.quanlybanvemaybay.repository.HomepageContentRepository homepageContentRepository;
    private final FlightServiceItemRepository serviceRepository;
    private final TeamMemberRepository teamRepository;
    private final com.example.quanlybanvemaybay.repository.MediaAssetRepository mediaAssetRepository;
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/banners/";

    public AdminVisualEditorController(BannerRepository bannerRepository, 
                                       com.example.quanlybanvemaybay.repository.HomepageContentRepository homepageContentRepository,
                                       FlightServiceItemRepository serviceRepository,
                                       TeamMemberRepository teamRepository,
                                       com.example.quanlybanvemaybay.repository.MediaAssetRepository mediaAssetRepository) {
        this.bannerRepository = bannerRepository;
        this.homepageContentRepository = homepageContentRepository;
        this.serviceRepository = serviceRepository;
        this.teamRepository = teamRepository;
        this.mediaAssetRepository = mediaAssetRepository;
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

            String fileUrl = "/uploads/banners/" + fileName;

            // Save to MediaAsset table
            com.example.quanlybanvemaybay.entity.MediaAsset asset = new com.example.quanlybanvemaybay.entity.MediaAsset();
            asset.setFileName(fileName);
            asset.setFileUrl(fileUrl);
            asset.setImageType("DESIGN_ASSET");
            mediaAssetRepository.save(asset);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "url", fileUrl
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

    @PostMapping("/save-text")
    public ResponseEntity<?> saveTextContent(@RequestParam("sectionKey") String sectionKey,
                                             @RequestParam("htmlContent") String htmlContent) {
        Optional<com.example.quanlybanvemaybay.entity.HomepageContent> existing = homepageContentRepository.findBySectionKey(sectionKey);
        com.example.quanlybanvemaybay.entity.HomepageContent content = existing.orElse(new com.example.quanlybanvemaybay.entity.HomepageContent());
        content.setSectionKey(sectionKey);
        content.setHtmlContent(htmlContent);
        homepageContentRepository.save(content);

        return ResponseEntity.ok(Map.of("success", true, "message", "Text content saved successfully"));
    }

    @PostMapping("/delete-entity")
    public ResponseEntity<?> deleteEntity(@RequestParam("type") String type, @RequestParam("id") Long id) {
        try {
            switch (type) {
                case "banner":
                    bannerRepository.deleteById(id);
                    break;
                case "service":
                    serviceRepository.deleteById(id);
                    break;
                case "team":
                    teamRepository.deleteById(id);
                    break;
                default:
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid entity type"));
            }
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error deleting: " + e.getMessage()));
        }
    }

    @PostMapping("/add-banner")
    public ResponseEntity<?> addBanner(@RequestParam("title") String title,
                                       @RequestParam("subtitle") String subtitle,
                                       @RequestParam("imageUrl") String imageUrl) {
        Banner banner = new Banner();
        banner.setTitle(title);
        banner.setSubtitle(subtitle);
        banner.setImageUrl(imageUrl);
        banner.setPosition("DESTINATION"); // Default for custom added banners
        banner.setIsActive(true);
        bannerRepository.save(banner);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/update-banner")
    public ResponseEntity<?> updateBanner(@RequestParam("id") Long id,
                                          @RequestParam("title") String title,
                                          @RequestParam("subtitle") String subtitle,
                                          @RequestParam("imageUrl") String imageUrl) {
        Optional<Banner> opt = bannerRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Không tìm thấy banner!"));
        }
        Banner banner = opt.get();
        banner.setTitle(title);
        banner.setSubtitle(subtitle);
        if (imageUrl != null && !imageUrl.isBlank()) {
            banner.setImageUrl(imageUrl);
        }
        bannerRepository.save(banner);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/add-carousel")
    public ResponseEntity<?> addCarouselSlide(@RequestParam("title") String title,
                                              @RequestParam("imageUrl") String imageUrl) {
        long count = bannerRepository.findAll().stream()
                .filter(b -> "HOME_CAROUSEL".equals(b.getPosition()) && Boolean.TRUE.equals(b.getIsActive()))
                .count();
        if (count >= 5) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Tối đa 5 ảnh trong carousel!"));
        }
        Banner banner = new Banner();
        banner.setTitle(title != null && !title.isEmpty() ? title : "Carousel");
        banner.setSubtitle("");
        banner.setImageUrl(imageUrl);
        banner.setPosition("HOME_CAROUSEL");
        banner.setIsActive(true);
        Banner saved = bannerRepository.save(banner);
        // Trả về id để frontend gắn nút xóa ngay mà không cần reload
        return ResponseEntity.ok(Map.of("success", true, "id", saved.getId()));
    }

    @PostMapping("/add-service")
    public ResponseEntity<?> addService(@RequestParam("title") String title,
                                        @RequestParam("description") String description,
                                        @RequestParam(value = "iconClass", required = false, defaultValue = "") String iconClass,
                                        @RequestParam(value = "imageUrl", required = false, defaultValue = "") String imageUrl) {
        FlightServiceItem service = new FlightServiceItem();
        service.setTitle(title);
        service.setDescription(description);
        service.setIconClass(iconClass);
        service.setImageUrl(imageUrl);
        service.setIsActive(true);
        serviceRepository.save(service);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/add-team")
    public ResponseEntity<?> addTeamMember(@RequestParam("name") String name,
                                           @RequestParam("role") String role,
                                           @RequestParam("imageUrl") String imageUrl) {
        TeamMember member = new TeamMember();
        member.setName(name);
        member.setRole(role);
        member.setImageUrl(imageUrl);
        member.setIsActive(true);
        teamRepository.save(member);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
