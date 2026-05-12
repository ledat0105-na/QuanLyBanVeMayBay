package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.repository.BannerRepository;
import com.example.quanlybanvemaybay.repository.FlightServiceItemRepository;
import com.example.quanlybanvemaybay.repository.TeamMemberRepository;
import com.example.quanlybanvemaybay.repository.UserRepository;
import com.example.quanlybanvemaybay.service.itf.AirportService;
import com.example.quanlybanvemaybay.entity.Airport;
import com.example.quanlybanvemaybay.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final AirportService airportService;
    private final BannerRepository bannerRepository;
    private final FlightServiceItemRepository serviceRepository;
    private final TeamMemberRepository teamRepository;
    private final UserRepository userRepository;
    private final com.example.quanlybanvemaybay.repository.HomepageContentRepository homepageContentRepository;

    public HomeController(AirportService airportService, BannerRepository bannerRepository,
                          FlightServiceItemRepository serviceRepository, TeamMemberRepository teamRepository,
                          UserRepository userRepository, com.example.quanlybanvemaybay.repository.HomepageContentRepository homepageContentRepository) {
        this.airportService = airportService;
        this.bannerRepository = bannerRepository;
        this.serviceRepository = serviceRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.homepageContentRepository = homepageContentRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Airport> allAirports = airportService.findAll();
        List<Airport> domestic = allAirports.stream()
            .filter(a -> a.getCountry() != null && 
                 (a.getCountry().toLowerCase().contains("vietnam") || a.getCountry().toLowerCase().contains("việt nam")))
            .collect(Collectors.toList());
        List<Airport> international = allAirports.stream()
            .filter(a -> a.getCountry() == null || 
                 (!a.getCountry().toLowerCase().contains("vietnam") && !a.getCountry().toLowerCase().contains("việt nam")))
            .collect(Collectors.toList());

        model.addAttribute("domesticAirports", domestic);
        model.addAttribute("internationalAirports", international);
        
        List<com.example.quanlybanvemaybay.entity.Banner> allBanners = bannerRepository.findByIsActiveTrue();
        model.addAttribute("banners", allBanners);
        
        
        String rawHomeAbout1 = allBanners.stream().filter(b -> "HOME_ABOUT_1".equals(b.getPosition())).findFirst().map(com.example.quanlybanvemaybay.entity.Banner::getImageUrl).orElse("/img/hero-slider-2.jpg");
        String rawHomeAbout2 = allBanners.stream().filter(b -> "HOME_ABOUT_2".equals(b.getPosition())).findFirst().map(com.example.quanlybanvemaybay.entity.Banner::getImageUrl).orElse("/img/hero-slider-3.jpg");
        
        model.addAttribute("homeAbout1", convertToEmbedUrl(rawHomeAbout1));
        model.addAttribute("homeAbout2", convertToEmbedUrl(rawHomeAbout2));
        model.addAttribute("isHomeAbout1Video", isVideoUrl(rawHomeAbout1));
        model.addAttribute("isHomeAbout2Video", isVideoUrl(rawHomeAbout2));
        
        model.addAttribute("services", serviceRepository.findByIsActiveTrue());
        model.addAttribute("teamMembers", teamRepository.findByIsActiveTrue());

        List<com.example.quanlybanvemaybay.entity.HomepageContent> contents = homepageContentRepository.findAll();
        Map<String, String> contentMap = contents.stream().collect(Collectors.toMap(com.example.quanlybanvemaybay.entity.HomepageContent::getSectionKey, com.example.quanlybanvemaybay.entity.HomepageContent::getHtmlContent));

        model.addAttribute("homeHeroTitle", contentMap.getOrDefault("home_hero_title", "Hệ Thống Đặt <span class=\"text-primary\">Vé Máy Bay</span> SkyTravel"));
        model.addAttribute("homeHeroSubtitle", contentMap.getOrDefault("home_hero_subtitle", "Nhanh Chóng - Tiết Kiệm - An Toàn"));
        model.addAttribute("homeServicesTitle", contentMap.getOrDefault("home_services_title", "Đẳng Cấp Trên Từng Chuyến Bay"));
        model.addAttribute("homeTeamTitle", contentMap.getOrDefault("home_team_title", "Đội Ngũ Nhân Sự"));
        model.addAttribute("homeTeamSubtitle", contentMap.getOrDefault("home_team_subtitle", "Gặp gỡ những phi công và chuyên gia hàng không tận tâm, mang lại sự an toàn tuyệt đối cho chuyến bay của bạn."));

        return "home";
    }

    private boolean isVideoUrl(String url) {
        if (url == null) return false;
        return url.contains("youtube.com") || url.contains("youtu.be");
    }

    private String convertToEmbedUrl(String url) {
        if (url == null) return null;
        if (url.contains("youtube.com/watch?v=")) {
            String videoId = url.substring(url.indexOf("v=") + 2);
            int ampersandPos = videoId.indexOf("&");
            if (ampersandPos != -1) {
                videoId = videoId.substring(0, ampersandPos);
            }
            return "https://www.youtube.com/embed/" + videoId;
        } else if (url.contains("youtu.be/")) {
            String videoId = url.substring(url.indexOf("youtu.be/") + 9);
            int queryPos = videoId.indexOf("?");
            if (queryPos != -1) {
                videoId = videoId.substring(0, queryPos);
            }
            return "https://www.youtube.com/embed/" + videoId;
        }
        return url;
    }

    @GetMapping("/admin/visual-editor")
    public String visualEditor(Model model) {
        model.addAttribute("isEditorMode", true);
        return home(model);
    }

    @GetMapping("/fix-pass")
    @ResponseBody
    public String fixPasswords() {
        List<User> users = userRepository.findAll();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("123456");
        int count = 0;
        for (User u : users) {
             u.setPassword(hash);
             userRepository.save(u);
             count++;
        }
        return "<h2 style='color:green; padding: 20px;'>Thành công! Đã mã hóa BCrypt (Reset về '123456') cho " + count + " tài khoản. <br><br><a href='/login'>Ấn vào đây để Đăng Nhập</a></h2>";
    }
}
