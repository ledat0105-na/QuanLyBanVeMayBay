package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.dto.request.CreateStaffRequest;
import com.example.quanlybanvemaybay.service.itf.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quanlybanvemaybay.service.itf.AirlineService;
import com.example.quanlybanvemaybay.entity.Airline;
import com.example.quanlybanvemaybay.service.itf.AirportService;
import com.example.quanlybanvemaybay.entity.Airport;
import com.example.quanlybanvemaybay.service.itf.FlightService;
import com.example.quanlybanvemaybay.entity.Flight;
import com.example.quanlybanvemaybay.service.itf.PromotionService;
import com.example.quanlybanvemaybay.entity.Promotion;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final AirlineService airlineService;
    private final AirportService airportService;
    private final FlightService flightService;
    private final PromotionService promotionService;
    private final com.example.quanlybanvemaybay.service.itf.EmailService emailService;

    public AdminController(UserService userService, AirlineService airlineService, AirportService airportService, FlightService flightService, PromotionService promotionService, com.example.quanlybanvemaybay.service.itf.EmailService emailService) {
        this.userService = userService;
        this.airlineService = airlineService;
        this.airportService = airportService;
        this.flightService = flightService;
        this.promotionService = promotionService;
        this.emailService = emailService;
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("createStaffRequest", new CreateStaffRequest());
        return "admin/manage-users";
    }

    @PostMapping("/users/create-staff")
    public String createStaff(
            @Valid @ModelAttribute("createStaffRequest") CreateStaffRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
            
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng nhập đầy đủ thông tin hợp lệ");
            return "redirect:/admin/users";
        }

        try {
            userService.createStaff(request);
            redirectAttributes.addFlashAttribute("success", "Tạo tài khoản Staff thành công!");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tạo tài khoản");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle-lock")
    public String toggleLockUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleLockUser(id);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/reset-password")
    public String resetPassword(
            @PathVariable("id") Long id,
            @RequestParam("newPassword") String newPassword,
            RedirectAttributes redirectAttributes) {
            
        if (newPassword == null || newPassword.isBlank() || newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự");
            return "redirect:/admin/users";
        }
        
        try {
            userService.resetUserPassword(id, newPassword);
            com.example.quanlybanvemaybay.entity.User user = userService.getUserById(id);
            emailService.sendPasswordResetByAdminEmail(user.getEmail(), user.getUsername(), newPassword);
            redirectAttributes.addFlashAttribute("success", "Đặt lại mật khẩu thành công và đã gửi email cho khách hàng!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/update")
    public String updateUser(
            @PathVariable("id") Long id,
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserAdmin(id, fullName, email, phone);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin người dùng thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/airlines")
    public String manageAirlines(Model model) {
        model.addAttribute("airlines", airlineService.findAll());
        model.addAttribute("airline", new Airline());
        return "admin/manage-airlines";
    }

    @PostMapping("/airlines/save")
    public String saveAirline(@ModelAttribute("airline") Airline airline, RedirectAttributes redirectAttributes) {
        try {
            airlineService.save(airline);
            redirectAttributes.addFlashAttribute("success", "Cập nhật hãng bay thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
        }
        return "redirect:/admin/airlines";
    }

    @PostMapping("/airlines/delete/{id}")
    public String deleteAirline(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            airlineService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa hãng bay thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
        }
        return "redirect:/admin/airlines";
    }

    @GetMapping("/airports")
    public String manageAirports(Model model) {
        model.addAttribute("airports", airportService.findAll());
        model.addAttribute("airport", new Airport());
        return "admin/manage-airports";
    }

    @PostMapping("/airports/save")
    public String saveAirport(@ModelAttribute("airport") Airport airport, RedirectAttributes redirectAttributes) {
        try {
            airportService.save(airport);
            redirectAttributes.addFlashAttribute("success", "Cập nhật sân bay thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
        }
        return "redirect:/admin/airports";
    }

    @PostMapping("/airports/delete/{id}")
    public String deleteAirport(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            airportService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa sân bay thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
        }
        return "redirect:/admin/airports";
    }

    private boolean isVietnam(String country) {
        if (country == null) return false;
        String lower = country.trim().toLowerCase();
        return lower.equals("vietnam") || lower.equals("việt nam") || lower.equals("vn") || lower.equals("viet nam");
    }

    @GetMapping("/flights")
    public String manageFlights(Model model) {
        java.util.List<Flight> all = flightService.findAll();
        
        all.sort((f1, f2) -> f2.getId().compareTo(f1.getId()));

        java.util.List<Flight> domestic = all.stream()
            .filter(f -> f.getDepartureAirport() != null && f.getArrivalAirport() != null
                && isVietnam(f.getDepartureAirport().getCountry())
                && isVietnam(f.getArrivalAirport().getCountry()))
            .toList();

        java.util.List<Flight> international = all.stream()
            .filter(f -> f.getDepartureAirport() == null || f.getArrivalAirport() == null
                || !isVietnam(f.getDepartureAirport().getCountry())
                || !isVietnam(f.getArrivalAirport().getCountry()))
            .toList();


        model.addAttribute("flights", all); 
        model.addAttribute("domesticFlights", domestic);
        model.addAttribute("internationalFlights", international);
        model.addAttribute("airlines", airlineService.findAll());
        model.addAttribute("airports", airportService.findAll());
        model.addAttribute("flight", new Flight());
        return "admin/manage-flights";
    }

    @PostMapping("/flights/save")
    public String saveFlight(@ModelAttribute("flight") Flight flight, RedirectAttributes redirectAttributes) {
        try {
            flightService.save(flight);
            redirectAttributes.addFlashAttribute("success", "Lưu chuyến bay thành công!");
        } catch (Exception ex) {
            ex.printStackTrace(); 
            try {
                java.nio.file.Files.writeString(java.nio.file.Paths.get("error_debug.log"), 
                    ex.getMessage() + "\n" + java.util.Arrays.toString(ex.getStackTrace()));
            } catch (Exception ignored) {}
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
        }
        return "redirect:/admin/flights";
    }

    @PostMapping("/flights/cancel/{id}")
    public String cancelFlight(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            flightService.cancelFlight(id);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái chuyến bay thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
        }
        return "redirect:/admin/flights";
    }

    @GetMapping("/promotions")
    public String managePromotions(Model model) {
        model.addAttribute("promotions", promotionService.findAll());
        model.addAttribute("promotion", new Promotion());
        return "admin/manage-promotions";
    }

    @PostMapping("/promotions/save")
    public String savePromotion(@ModelAttribute("promotion") Promotion promotion, RedirectAttributes redirectAttributes) {
        try {
            promotionService.save(promotion);
            redirectAttributes.addFlashAttribute("success", "Cập nhật mã khuyến mãi thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
        }
        return "redirect:/admin/promotions";
    }

    @PostMapping("/promotions/toggle/{id}")
    public String togglePromotionStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            promotionService.toggleStatus(id);
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật trạng thái khuyến mãi!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
        }
        return "redirect:/admin/promotions";
    }
}
