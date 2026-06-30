package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.entity.Booking;
import com.example.quanlybanvemaybay.entity.User;
import com.example.quanlybanvemaybay.repository.UserRepository;
import com.example.quanlybanvemaybay.service.itf.BookingService;
import com.example.quanlybanvemaybay.service.itf.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final BookingService bookingService;
    private final UserService userService;
    private final UserRepository userRepository;

    public ProfileController(BookingService bookingService, UserService userService, UserRepository userRepository) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/bookings")
    public String myBookings(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        String username = principal.getName();
        List<Booking> bookings = bookingService.getBookingsByUsername(username);
        
        model.addAttribute("bookings", bookings);
        return "profile/bookings";
    }

    @GetMapping("/info")
    public String profileInfo(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "profile/info";
    }

    @PostMapping("/update-info")
    public String updateInfo(@RequestParam("fullName") String fullName, @RequestParam("phone") String phone, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        try {
            userService.updateUserInfo(principal.getName(), fullName, phone);
            redirectAttributes.addFlashAttribute("successDetails", "Cập nhật thông tin thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorDetails", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/profile/info";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorSecurity", "Mật khẩu xác nhận không khớp!");
            return "redirect:/profile/info";
        }
        try {
            userService.changePassword(principal.getName(), oldPassword, newPassword);
            redirectAttributes.addFlashAttribute("successSecurity", "Đổi mật khẩu thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorSecurity", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorSecurity", "Có lỗi xảy ra khi đổi mật khẩu.");
        }
        return "redirect:/profile/info";
    }

    @PostMapping("/bookings/{id}/checkin")
    public String checkIn(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        
        try {
            Booking booking = bookingService.findById(id);
            if (booking == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin đặt vé!");
                return "redirect:/profile/bookings";
            }
            
            // Check ownership
            if (booking.getUser() == null || !booking.getUser().getUsername().equals(principal.getName())) {
                redirectAttributes.addFlashAttribute("error", "Bạn không có quyền thực hiện thao tác này!");
                return "redirect:/profile/bookings";
            }
            
            if (booking.isCheckedIn()) {
                redirectAttributes.addFlashAttribute("success", "Bạn đã làm thủ tục check-in cho vé này rồi!");
                return "redirect:/profile/bookings";
            }
            
            if (!booking.isCheckInAllowed()) {
                redirectAttributes.addFlashAttribute("error", "Thời gian làm thủ tục không hợp lệ. Check-in chỉ mở từ 23 giờ đến 1 giờ trước giờ khởi hành!");
                return "redirect:/profile/bookings";
            }
            
            bookingService.checkInBooking(id);
            redirectAttributes.addFlashAttribute("success", "Làm thủ tục check-in thành công cho mã đặt chỗ: " + booking.getBookingCode() + "!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/profile/bookings";
    }
}
