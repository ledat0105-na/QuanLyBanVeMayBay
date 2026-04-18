package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.entity.Booking;
import com.example.quanlybanvemaybay.entity.Payment;
import com.example.quanlybanvemaybay.repository.PaymentRepository;
import com.example.quanlybanvemaybay.service.itf.BookingService;
import com.example.quanlybanvemaybay.service.itf.EmailService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    private final BookingService bookingService;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;

    public AdminBookingController(BookingService bookingService, PaymentRepository paymentRepository, EmailService emailService) {
        this.bookingService = bookingService;
        this.paymentRepository = paymentRepository;
        this.emailService = emailService;
    }

    @GetMapping
    public String listBookings(Model model) {
        List<Booking> bookings = bookingService.getAllBookings();
        model.addAttribute("bookings", bookings);
        return "admin/manage-bookings";
    }

    @GetMapping("/{id}")
    public String bookingDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Booking booking = bookingService.findById(id);
        if (booking == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy vé!");
            return "redirect:/admin/bookings";
        }
        model.addAttribute("booking", booking);
        return "admin/booking-details";
    }

    @PostMapping("/{id}/confirm-payment")
    public String confirmPayment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Booking booking = bookingService.updateBookingStatus(id, "CONFIRMED");
            // Also mark payments as COMPLETED
            for (Payment p : booking.getPayments()) {
                if ("PENDING".equals(p.getPaymentStatus())) {
                    p.setPaymentStatus("COMPLETED");
                    paymentRepository.save(p);
                }
            }
            // Send confirmation email to the user
            if (booking.getUser() != null && booking.getUser().getEmail() != null) {
                emailService.sendBookingConfirmationEmail(booking, booking.getUser().getEmail());
            }
            redirectAttributes.addFlashAttribute("success", "Đã xác nhận thanh toán thành công và gửi Email vé đến khách hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xác nhận thanh toán: " + e.getMessage());
        }
        return "redirect:/admin/bookings/" + id;
    }

    @PostMapping("/{id}/reject-payment")
    public String rejectPayment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Booking booking = bookingService.updateBookingStatus(id, "CANCELLED");
            for (Payment p : booking.getPayments()) {
                if ("PENDING".equals(p.getPaymentStatus())) {
                    p.setPaymentStatus("FAILED");
                    paymentRepository.save(p);
                }
            }
            redirectAttributes.addFlashAttribute("success", "Đã từ chối thanh toán và hủy vé!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/bookings/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.updateBookingStatus(id, "CANCELLED");
            redirectAttributes.addFlashAttribute("success", "Đã hủy vé thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi hủy vé: " + e.getMessage());
        }
        return "redirect:/admin/bookings/" + id;
    }

    @PostMapping("/{id}/refund")
    public String refundBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.updateBookingStatus(id, "REFUNDED");
            redirectAttributes.addFlashAttribute("success", "Đã đánh dấu hoàn tiền cho vé này!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi hoàn tiền: " + e.getMessage());
        }
        return "redirect:/admin/bookings/" + id;
    }

    @PostMapping("/passengers/edit")
    public String editPassenger(
            @RequestParam("bookingId") Long bookingId,
            @RequestParam("passengerId") Long passengerId,
            @RequestParam("fullName") String fullName,
            @RequestParam("gender") String gender,
            @RequestParam("dateOfBirth") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
            @RequestParam(value = "passportNumber", required = false) String passportNumber,
            RedirectAttributes redirectAttributes) {
        try {
            bookingService.updatePassengerInfo(passengerId, fullName, gender, dateOfBirth, passportNumber);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin hành khách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật hành khách: " + e.getMessage());
        }
        return "redirect:/admin/bookings/" + bookingId;
    }
}
