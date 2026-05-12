package com.example.quanlybanvemaybay.config;

import com.example.quanlybanvemaybay.entity.Banner;
import com.example.quanlybanvemaybay.repository.BannerRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.List;

import com.example.quanlybanvemaybay.entity.Notification;
import com.example.quanlybanvemaybay.entity.User;
import com.example.quanlybanvemaybay.repository.NotificationRepository;
import com.example.quanlybanvemaybay.repository.UserRepository;
import com.example.quanlybanvemaybay.repository.HomepageContentRepository;
import com.example.quanlybanvemaybay.repository.ContactMessageRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final BannerRepository bannerRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final HomepageContentRepository homepageContentRepository;
    private final ContactMessageRepository contactMessageRepository;

    public GlobalControllerAdvice(BannerRepository bannerRepository, 
                                  NotificationRepository notificationRepository, 
                                  UserRepository userRepository,
                                  HomepageContentRepository homepageContentRepository,
                                  ContactMessageRepository contactMessageRepository) {
        this.bannerRepository = bannerRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.homepageContentRepository = homepageContentRepository;
        this.contactMessageRepository = contactMessageRepository;
    }

    @ModelAttribute
    public void addFooterAttributes(org.springframework.ui.Model model) {
        List<com.example.quanlybanvemaybay.entity.HomepageContent> contents = homepageContentRepository.findAll();
        Map<String, String> contentMap = contents.stream().collect(Collectors.toMap(
            com.example.quanlybanvemaybay.entity.HomepageContent::getSectionKey, 
            com.example.quanlybanvemaybay.entity.HomepageContent::getHtmlContent,
            (existing, replacement) -> existing
        ));

        model.addAttribute("footerAddress", contentMap.getOrDefault("footer_address", "123 Đường Cầu Giấy, Hà Nội, Việt Nam"));
        model.addAttribute("footerPhone", contentMap.getOrDefault("footer_phone", "+84 123 456 789"));
        model.addAttribute("footerEmail", contentMap.getOrDefault("footer_email", "contact@skytravel.com.vn"));
        model.addAttribute("footerDesc", contentMap.getOrDefault("footer_desc", "Hệ thống đặt vé máy bay trực tuyến uy tín, nhanh chóng và an toàn nhất."));
        model.addAttribute("footerTwitter", contentMap.getOrDefault("footer_twitter", "#"));
        model.addAttribute("footerFacebook", contentMap.getOrDefault("footer_facebook", "#"));
        model.addAttribute("footerYoutube", contentMap.getOrDefault("footer_youtube", "#"));
        model.addAttribute("footerInstagram", contentMap.getOrDefault("footer_instagram", "#"));
        model.addAttribute("footerMapUrl", contentMap.getOrDefault("footer_map_url", "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3834.453733221379!2d108.21948521096706!3d16.03219258410507!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x314219ee598df9c5%3A0xaadb53409be7c909!2zVHLGsOG7nW5nIMSQ4bqhaSBo4buNYyBLaeG6v24gdHLDumMgxJDDoCBO4bq1bmc!5e0!3m2!1svi!2s!4v1715081800000!5m2!1svi!2s"));
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
                    (existing, replacement) -> existing
                ));
    }

    @ModelAttribute("userNotifications")
    public List<Notification> getNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String username = auth.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
            }
        }
        return Collections.emptyList();
    }

    @ModelAttribute("unreadNotificationCount")
    public int getUnreadCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String username = auth.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                return notificationRepository.countByUserIdAndIsReadFalse(user.getId());
            }
        }
        return 0;
    }

    @ModelAttribute("unreadContactCount")
    public int getUnreadContactCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            boolean isManagement = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_STAFF"));
            if (isManagement) {
                return (int) contactMessageRepository.countByIsReadFalse();
            }
        }
        return 0;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(org.springframework.validation.BindException.class)
    public String handleBindException(org.springframework.validation.BindException ex, HttpServletRequest request) {
        try {
            StringBuilder sb = new StringBuilder("Bind Errors:\n");
            ex.getFieldErrors().forEach(e -> sb.append(e.getField()).append(" : ").append(e.getDefaultMessage()).append("\n"));
            java.nio.file.Files.writeString(java.nio.file.Paths.get("bind_error_debug.log"), sb.toString());
        } catch (Exception ignored) {}
        
        
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
}
