package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.entity.ContactMessage;
import com.example.quanlybanvemaybay.repository.BannerRepository;
import com.example.quanlybanvemaybay.repository.ContactMessageRepository;
import com.example.quanlybanvemaybay.repository.FlightServiceItemRepository;
import com.example.quanlybanvemaybay.repository.TeamMemberRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PageController {

    private final TeamMemberRepository teamRepo;
    private final FlightServiceItemRepository serviceRepo;
    private final BannerRepository bannerRepo;
    private final ContactMessageRepository contactRepo;
    private final com.example.quanlybanvemaybay.repository.UserRepository userRepo;
    private final com.example.quanlybanvemaybay.repository.NotificationRepository notifRepo;

    public PageController(TeamMemberRepository teamRepo, FlightServiceItemRepository serviceRepo,
                          BannerRepository bannerRepo, ContactMessageRepository contactRepo,
                          com.example.quanlybanvemaybay.repository.UserRepository userRepo,
                          com.example.quanlybanvemaybay.repository.NotificationRepository notifRepo) {
        this.teamRepo = teamRepo;
        this.serviceRepo = serviceRepo;
        this.bannerRepo = bannerRepo;
        this.contactRepo = contactRepo;
        this.userRepo = userRepo;
        this.notifRepo = notifRepo;
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("teamMembers", teamRepo.findByIsActiveTrue());
        return "pages/about";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("services", serviceRepo.findByIsActiveTrue());
        return "pages/services";
    }

    @GetMapping("/projects")
    public String projects(Model model) {
        model.addAttribute("banners", bannerRepo.findByIsActiveTrue());
        return "pages/projects";
    }

    @GetMapping("/contact")
    public String contact() {
        return "pages/contact";
    }

    @PostMapping("/contact/send")
    public String sendMessage(@ModelAttribute ContactMessage message, RedirectAttributes ra) {
        contactRepo.save(message);

        
        java.util.List<com.example.quanlybanvemaybay.entity.User> staffList = userRepo.findAll().stream()
            .filter(u -> u.getRole() != null && (u.getRole().getRoleName().equals("ADMIN") || u.getRole().getRoleName().equals("STAFF")))
            .toList();
        
        String groupId = java.util.UUID.randomUUID().toString();
        for (com.example.quanlybanvemaybay.entity.User staff : staffList) {
            com.example.quanlybanvemaybay.entity.Notification notif = new com.example.quanlybanvemaybay.entity.Notification();
            notif.setUser(staff);
            notif.setTitle("Tin nhắn mới từ khách hàng");
            notif.setMessage("Khách hàng " + message.getSenderName() + " vừa gửi tin nhắn liên hệ.");
            notif.setIsRead(false);
            notif.setCreatedAt(java.time.LocalDateTime.now());
            notif.setGroupId(groupId);
            notifRepo.save(notif);
        }

        ra.addFlashAttribute("successMessage", "Cảm ơn bạn đã liên hệ! Hộp thư đã nhận được phản hồi và sẽ liên lạc lại sớm nhất.");
        return "redirect:/contact";
    }
}
