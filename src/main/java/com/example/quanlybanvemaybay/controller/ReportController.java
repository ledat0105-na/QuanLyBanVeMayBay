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
    public String viewReport(Model model,
                             @org.springframework.web.bind.annotation.RequestParam(required = false) String filterType,
                             @org.springframework.web.bind.annotation.RequestParam(required = false) String filterDate) {
        java.time.LocalDateTime startDate;
        java.time.LocalDateTime endDate;
        
        if (filterType == null || filterType.isEmpty()) {
            filterType = "DAY";
        }
        
        java.time.LocalDate date = java.time.LocalDate.now();
        if (filterDate != null && !filterDate.isEmpty()) {
            try {
                if ("DAY".equals(filterType)) {
                    date = java.time.LocalDate.parse(filterDate);
                } else if ("MONTH".equals(filterType)) {
                    java.time.YearMonth ym = java.time.YearMonth.parse(filterDate);
                    date = ym.atDay(1);
                } else if ("YEAR".equals(filterType)) {
                    date = java.time.LocalDate.of(Integer.parseInt(filterDate), 1, 1);
                } else if ("QUARTER".equals(filterType)) {
                    String[] parts = filterDate.split("-Q");
                    int year = Integer.parseInt(parts[0]);
                    int quarter = Integer.parseInt(parts[1]);
                    date = java.time.LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
                }
            } catch (Exception e) {
                date = java.time.LocalDate.now();
            }
        } else {
            if ("MONTH".equals(filterType)) filterDate = java.time.YearMonth.now().toString();
            else if ("YEAR".equals(filterType)) filterDate = String.valueOf(date.getYear());
            else if ("QUARTER".equals(filterType)) filterDate = date.getYear() + "-Q" + ((date.getMonthValue() - 1) / 3 + 1);
            else filterDate = date.toString();
        }

        if ("DAY".equals(filterType)) {
            startDate = date.atStartOfDay();
            endDate = date.atTime(23, 59, 59);
        } else if ("MONTH".equals(filterType)) {
            startDate = date.withDayOfMonth(1).atStartOfDay();
            endDate = date.withDayOfMonth(date.lengthOfMonth()).atTime(23, 59, 59);
        } else if ("YEAR".equals(filterType)) {
            startDate = date.withDayOfYear(1).atStartOfDay();
            endDate = date.withDayOfYear(date.lengthOfYear()).atTime(23, 59, 59);
        } else if ("QUARTER".equals(filterType)) {
            startDate = date.withDayOfMonth(1).atStartOfDay();
            endDate = date.plusMonths(2).withDayOfMonth(date.plusMonths(2).lengthOfMonth()).atTime(23, 59, 59);
        } else {
            startDate = date.atStartOfDay();
            endDate = date.atTime(23, 59, 59);
        }

        long totalUsers = userRepository.countByCreatedAtBetween(startDate, endDate);
        long totalBookings = bookingRepository.countByBookingDateBetween(startDate, endDate);
        BigDecimal totalRevenue = paymentRepository.calculateTotalRevenueBetween(startDate, endDate);
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }
        
        List<Object[]> popularFlights = bookingRepository.findPopularFlightsBetween(startDate, endDate, PageRequest.of(0, 5));

        
        List<String> chartLabels = new java.util.ArrayList<>();
        List<BigDecimal> chartData = new java.util.ArrayList<>();

        if ("YEAR".equals(filterType)) {
            
            int year = date.getYear();
            for (int m = 1; m <= 12; m++) {
                java.time.YearMonth ym = java.time.YearMonth.of(year, m);
                chartLabels.add("Th " + m);
                java.time.LocalDateTime mStart = ym.atDay(1).atStartOfDay();
                java.time.LocalDateTime mEnd = ym.atEndOfMonth().atTime(23, 59, 59);
                BigDecimal rev = paymentRepository.calculateTotalRevenueBetween(mStart, mEnd);
                chartData.add(rev != null ? rev : BigDecimal.ZERO);
            }
        } else if ("QUARTER".equals(filterType)) {
            
            int startMonth = ((date.getMonthValue() - 1) / 3) * 3 + 1;
            for (int i = 0; i < 3; i++) {
                java.time.YearMonth ym = java.time.YearMonth.of(date.getYear(), startMonth + i);
                chartLabels.add("Th " + ym.getMonthValue());
                java.time.LocalDateTime mStart = ym.atDay(1).atStartOfDay();
                java.time.LocalDateTime mEnd = ym.atEndOfMonth().atTime(23, 59, 59);
                BigDecimal rev = paymentRepository.calculateTotalRevenueBetween(mStart, mEnd);
                chartData.add(rev != null ? rev : BigDecimal.ZERO);
            }
        } else if ("MONTH".equals(filterType)) {
            
            int days = date.lengthOfMonth();
            for (int d = 1; d <= days; d++) {
                chartLabels.add(String.valueOf(d));
                java.time.LocalDateTime dStart = date.withDayOfMonth(d).atStartOfDay();
                java.time.LocalDateTime dEnd = date.withDayOfMonth(d).atTime(23, 59, 59);
                BigDecimal rev = paymentRepository.calculateTotalRevenueBetween(dStart, dEnd);
                chartData.add(rev != null ? rev : BigDecimal.ZERO);
            }
        } else {
            
            for (int i = 6; i >= 0; i--) {
                java.time.LocalDate d = date.minusDays(i);
                chartLabels.add(d.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM")));
                java.time.LocalDateTime dStart = d.atStartOfDay();
                java.time.LocalDateTime dEnd = d.atTime(23, 59, 59);
                BigDecimal rev = paymentRepository.calculateTotalRevenueBetween(dStart, dEnd);
                chartData.add(rev != null ? rev : BigDecimal.ZERO);
            }
        }

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("popularFlights", popularFlights);
        model.addAttribute("filterType", filterType);
        model.addAttribute("filterDate", filterDate);
        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartData", chartData);

        return "admin/report-dashboard";
    }
}
