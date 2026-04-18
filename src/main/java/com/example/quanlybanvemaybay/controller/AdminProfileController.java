package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.entity.User;
import com.example.quanlybanvemaybay.repository.UserRepository;
import com.example.quanlybanvemaybay.service.itf.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.UUID;

@Controller
@RequestMapping("/admin/profile")
public class AdminProfileController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/avatars/";

    public AdminProfileController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String profile(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "admin/profile";
    }

    @PostMapping("/update")
    public String updateProfile(
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) return "redirect:/login";

        try {
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            user.setFullName(fullName);
            user.setPhone(phone);

            if (avatarFile != null && !avatarFile.isEmpty()) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String fileName = UUID.randomUUID().toString() + "_" + avatarFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(avatarFile.getInputStream(), filePath);
                user.setAvatarUrl("/uploads/avatars/" + fileName);
            }

            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successDetails", "Cập nhật thông tin thành công!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorDetails", "Lỗi upload ảnh: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorDetails", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) return "redirect:/login";

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorSecurity", "Mật khẩu xác nhận không khớp!");
            return "redirect:/admin/profile";
        }

        try {
            userService.changePassword(principal.getName(), oldPassword, newPassword);
            redirectAttributes.addFlashAttribute("successSecurity", "Đổi mật khẩu thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorSecurity", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorSecurity", "Có lỗi xảy ra khi đổi mật khẩu.");
        }

        return "redirect:/admin/profile";
    }
}
