package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.dto.request.RegisterRequest;
import com.example.quanlybanvemaybay.service.itf.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "returnUrl", required = false) String returnUrl, jakarta.servlet.http.HttpSession session) {
        if (returnUrl != null && !returnUrl.isBlank()) {
            session.setAttribute("returnUrl", returnUrl);
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(@RequestParam(value = "returnUrl", required = false) String returnUrl, Model model, jakarta.servlet.http.HttpSession session) {
        if (returnUrl != null && !returnUrl.isBlank()) {
            session.setAttribute("returnUrl", returnUrl);
        }
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerRequest") RegisterRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        if (request.getPassword() == null || !request.getPassword().equals(request.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "confirmPassword.mismatch", "Xác nhận mật khẩu không khớp");
            return "auth/register";
        }

        try {
            authService.register(request);
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("register.failed", ex.getMessage());
            return "auth/register";
        }

        redirectAttributes.addAttribute("registered", "true");
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPasswordSubmit(
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes
    ) {
        try {
            authService.checkUserExists(email);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/forgot-password";
        }

        redirectAttributes.addAttribute("email", email);
        return "redirect:/reset-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam(name = "email", required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordSubmit(
            @RequestParam("email") String email,
            @RequestParam("otpCode") String otpCode,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes
    ) {
        if (newPassword == null || newPassword.isBlank() || !newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới không hợp lệ hoặc xác nhận mật khẩu không khớp");
            return "redirect:/reset-password?email=" + email;
        }

        try {
            authService.resetPassword(email, otpCode, newPassword);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/reset-password?email=" + email;
        }

        redirectAttributes.addAttribute("reset", "true");
        return "redirect:/login";
    }

    @PostMapping("/api/auth/send-otp")
    @org.springframework.web.bind.annotation.ResponseBody
    public org.springframework.http.ResponseEntity<?> sendOtp(@RequestParam String email) {
        try {
            authService.sendPasswordResetOtp(email);
            return org.springframework.http.ResponseEntity.ok().body(java.util.Map.of("message", "Mã OTP đã được gửi!"));
        } catch (IllegalArgumentException ex) {
            return org.springframework.http.ResponseEntity.badRequest().body(java.util.Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/api/auth/reset-password")
    @org.springframework.web.bind.annotation.ResponseBody
    public org.springframework.http.ResponseEntity<?> resetPasswordApi(
            @RequestParam("email") String email,
            @RequestParam("otpCode") String otpCode,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword
    ) {
        if (newPassword == null || newPassword.isBlank() || !newPassword.equals(confirmPassword)) {
            return org.springframework.http.ResponseEntity.badRequest().body(java.util.Map.of("error", "Mật khẩu mới không hợp lệ hoặc xác nhận mật khẩu không khớp"));
        }

        try {
            authService.resetPassword(email, otpCode, newPassword);
            return org.springframework.http.ResponseEntity.ok().body(java.util.Map.of("message", "Đặt lại mật khẩu thành công!"));
        } catch (IllegalArgumentException ex) {
            return org.springframework.http.ResponseEntity.badRequest().body(java.util.Map.of("error", ex.getMessage()));
        }
    }
}
