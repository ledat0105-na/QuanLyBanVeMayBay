package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.repository.BookingRepository;
import com.example.quanlybanvemaybay.repository.PaymentRepository;
import com.example.quanlybanvemaybay.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class ReportController {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    public ReportController(UserRepository userRepository, BookingRepository bookingRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/report")
    public String viewReport(Model model) {
        long totalUsers = userRepository.count();
        long totalBookings = bookingRepository.count();
        BigDecimal totalRevenue = paymentRepository.calculateTotalRevenue();
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }
        
        List<Object[]> popularFlights = bookingRepository.findPopularFlights(PageRequest.of(0, 5));

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("popularFlights", popularFlights);

        return "admin/report-dashboard";
    }
}
