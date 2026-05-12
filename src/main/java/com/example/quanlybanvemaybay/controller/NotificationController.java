package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.entity.Notification;
import com.example.quanlybanvemaybay.entity.User;
import com.example.quanlybanvemaybay.repository.NotificationRepository;
import com.example.quanlybanvemaybay.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationController(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    
    public static class GroupedNotificationDto {
        private Long id;
        private String title;
        private String message;
        private LocalDateTime createdAt;
        private boolean isRead;
        private String recipientSummary;
        private String allRecipients;
        private boolean isGroup;

        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public boolean getIsRead() { return isRead; }
        public void setIsRead(boolean isRead) { this.isRead = isRead; }
        public String getRecipientSummary() { return recipientSummary; }
        public void setRecipientSummary(String recipientSummary) { this.recipientSummary = recipientSummary; }
        public String getAllRecipients() { return allRecipients; }
        public void setAllRecipients(String allRecipients) { this.allRecipients = allRecipients; }
        public boolean getIsGroup() { return isGroup; }
        public void setIsGroup(boolean isGroup) { this.isGroup = isGroup; }
    }

    
    @GetMapping("/admin/notifications")
    public String manageNotifications(Model model) {
        List<Notification> all = notificationRepository.findAllByOrderByCreatedAtDesc();
        
        
        java.util.Map<String, java.util.List<Notification>> groupedMap = new java.util.LinkedHashMap<>();
        for (Notification n : all) {
            String key = n.getTitle() + "|||" + n.getMessage();
            groupedMap.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(n);
        }

        List<GroupedNotificationDto> groupedDtos = new java.util.ArrayList<>();
        for (java.util.Map.Entry<String, java.util.List<Notification>> entry : groupedMap.entrySet()) {
            java.util.List<Notification> group = entry.getValue();
            Notification first = group.get(0);
            
            GroupedNotificationDto dto = new GroupedNotificationDto();
            dto.setId(first.getId()); 
            dto.setTitle(first.getTitle());
            dto.setMessage(first.getMessage());
            dto.setCreatedAt(first.getCreatedAt()); 
            dto.setIsRead(group.stream().allMatch(Notification::getIsRead)); 
            dto.setIsGroup(group.size() > 1 || first.getGroupId() != null);
            
            if (group.size() > 1) {
                dto.setRecipientSummary(group.size() + " người nhận");
            } else {
                dto.setRecipientSummary(first.getUser().getUsername());
            }

            java.util.List<String> usernames = group.stream().map(n -> n.getUser().getUsername()).toList();
            dto.setAllRecipients(String.join(", ", usernames));
            
            groupedDtos.add(dto);
        }

        model.addAttribute("notifications", groupedDtos);
        return "admin/manage-notifications";
    }

    
    @PostMapping("/admin/notifications/send")
    public String sendNotification(@RequestParam String title,
                                   @RequestParam String message,
                                   @RequestParam String target,
                                   @RequestParam(required = false) String targetUsername,
                                   RedirectAttributes redirectAttributes) {
        
        if ("ALL".equals(target)) {
            String gId = java.util.UUID.randomUUID().toString();
            List<User> customers = userRepository.findAll().stream()
                .filter(u -> u.getRole() != null && "USER".equals(u.getRole().getRoleName()))
                .toList();
            for (User user : customers) {
                Notification notif = new Notification();
                notif.setUser(user);
                notif.setTitle(title);
                notif.setMessage(message);
                notif.setIsRead(false);
                notif.setCreatedAt(LocalDateTime.now());
                notif.setGroupId(gId);
                notificationRepository.save(notif);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Đã gửi thông báo đến tất cả khách hàng!");
        } else if ("SPECIFIC".equals(target)) {
            Optional<User> userOpt = userRepository.findByUsername(targetUsername);
            if (userOpt.isPresent()) {
                Notification notif = new Notification();
                notif.setUser(userOpt.get());
                notif.setTitle(title);
                notif.setMessage(message);
                notif.setIsRead(false);
                notif.setCreatedAt(LocalDateTime.now());
                notificationRepository.save(notif);
                redirectAttributes.addFlashAttribute("successMessage", "Đã gửi thông báo đến: " + targetUsername);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng có tên đăng nhập: " + targetUsername);
            }
        }
        
        return "redirect:/admin/notifications";
    }

    
    @PostMapping("/admin/notifications/update")
    @org.springframework.transaction.annotation.Transactional
    public String updateNotification(@RequestParam Long id,
                                     @RequestParam String title,
                                     @RequestParam String message,
                                     RedirectAttributes redirectAttributes) {
        Optional<Notification> notifOpt = notificationRepository.findById(id);
        if (notifOpt.isPresent()) {
            Notification master = notifOpt.get();
            String oldTitle = master.getTitle();
            String oldMessage = master.getMessage();
            String finalMsg = message.startsWith("[Cập nhật]") ? message : "[Cập nhật]: " + message;
            LocalDateTime now = LocalDateTime.now();

            List<Notification> group = notificationRepository.findByTitleAndMessage(oldTitle, oldMessage);
            for (Notification n : group) {
                n.setTitle(title);
                n.setMessage(finalMsg);
                n.setCreatedAt(now);
                n.setIsRead(false);
                notificationRepository.save(n);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật " + group.size() + " thông báo thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông báo!");
        }
        return "redirect:/admin/notifications";
    }

    
    @PostMapping("/admin/notifications/delete/{id}")
    @org.springframework.transaction.annotation.Transactional
    public String deleteNotification(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Notification> notifOpt = notificationRepository.findById(id);
        if (notifOpt.isPresent()) {
            Notification master = notifOpt.get();
            notificationRepository.deleteByTitleAndMessage(master.getTitle(), master.getMessage());
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa toàn bộ thông báo thuộc nhóm này!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông báo!");
        }
        return "redirect:/admin/notifications";
    }

    
    @PostMapping("/api/notifications/read/{id}")
    @ResponseBody
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        Optional<Notification> notifOpt = notificationRepository.findById(id);
        if (notifOpt.isPresent()) {
            Notification notif = notifOpt.get();
            if (notif.getGroupId() != null) {
                
                List<Notification> group = notificationRepository.findByGroupId(notif.getGroupId());
                for (Notification n : group) {
                    n.setIsRead(true);
                    notificationRepository.save(n);
                }
            } else {
                notif.setIsRead(true);
                notificationRepository.save(notif);
            }
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    
    @GetMapping("/api/notifications/unread")
    @ResponseBody
    public ResponseEntity<List<java.util.Map<String, Object>>> getUnreadNotifications(java.security.Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.status(401).build();
        
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalse(userOpt.get().getId());
        List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        for(Notification n : unread) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", n.getId());
            map.put("title", n.getTitle());
            map.put("message", n.getMessage());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }
}
