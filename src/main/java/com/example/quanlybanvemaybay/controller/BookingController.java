package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.dto.BookingRequestDTO;
import com.example.quanlybanvemaybay.entity.Booking;
import com.example.quanlybanvemaybay.entity.Flight;
import com.example.quanlybanvemaybay.entity.Passenger;
import com.example.quanlybanvemaybay.repository.BaggageOptionRepository;
import com.example.quanlybanvemaybay.service.itf.BookingService;
import com.example.quanlybanvemaybay.service.itf.FlightService;
import com.example.quanlybanvemaybay.service.itf.EmailService;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.time.LocalDateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/booking")
@SessionAttributes("bookingReq")
public class BookingController {

    private final FlightService flightService;
    private final BaggageOptionRepository baggageOptionRepository;
    private final BookingService bookingService;
    private final EmailService emailService;
    private final com.example.quanlybanvemaybay.repository.UserRepository userRepo;
    private final com.example.quanlybanvemaybay.repository.NotificationRepository notifRepo;
    private final com.example.quanlybanvemaybay.repository.PromotionRepository promotionRepo;

    public BookingController(FlightService flightService, BaggageOptionRepository baggageOptionRepository, BookingService bookingService, EmailService emailService, com.example.quanlybanvemaybay.repository.UserRepository userRepo, com.example.quanlybanvemaybay.repository.NotificationRepository notifRepo, com.example.quanlybanvemaybay.repository.PromotionRepository promotionRepo) {
        this.flightService = flightService;
        this.baggageOptionRepository = baggageOptionRepository;
        this.bookingService = bookingService;
        this.emailService = emailService;
        this.userRepo = userRepo;
        this.notifRepo = notifRepo;
        this.promotionRepo = promotionRepo;
    }

    @GetMapping("/step1")
    public String step1(@RequestParam("flightId") Long flightId,
                        @RequestParam(value = "returnFlightId", required = false) Long returnFlightId,
                        @RequestParam(value = "passengers", defaultValue = "1") int numPassengers,
                        Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        Flight flight = flightService.findById(flightId);
        if (flight == null) {
            return "redirect:/";
        }

        
        if (flight.getDepartureTime() != null) {
            LocalDateTime cutoff = flight.getDepartureTime().minusHours(3);
            if (LocalDateTime.now().isAfter(cutoff)) {
                redirectAttributes.addFlashAttribute("flightExpiredMsg",
                    "Chuyến bay " + flight.getFlightNumber() + " đã hết hạn đặt vé! Chỉ mở bán đến trước giờ khởi hành 3 tiếng.");
                return "redirect:/";
            }
        }

        Flight returnFlight = null;
        if (returnFlightId != null) {
            returnFlight = flightService.findById(returnFlightId);
            if (returnFlight != null && returnFlight.getDepartureTime() != null) {
                LocalDateTime cutoff = returnFlight.getDepartureTime().minusHours(3);
                if (LocalDateTime.now().isAfter(cutoff)) {
                    redirectAttributes.addFlashAttribute("flightExpiredMsg",
                        "Chuyến bay về " + returnFlight.getFlightNumber() + " đã hết hạn đặt vé! Chỉ mở bán đến trước giờ khởi hành 3 tiếng.");
                    return "redirect:/";
                }
            }
        }

        BookingRequestDTO req = new BookingRequestDTO();
        req.setFlight(flight);
        req.setReturnFlight(returnFlight);
        req.setNumberOfPassengers(numPassengers);

        for (int i = 0; i < numPassengers; i++) {
            req.getPassengers().add(new Passenger());
        }

        session.setAttribute("bookingReq", req);
        model.addAttribute("req", req);

        return "booking/step1";
    }

    @PostMapping("/step1")
    public String processStep1(@ModelAttribute("req") BookingRequestDTO reqForm, HttpSession session, RedirectAttributes redirectAttributes) {
        BookingRequestDTO sessionReq = (BookingRequestDTO) session.getAttribute("bookingReq");
        if (sessionReq == null) {
            return "redirect:/";
        }

        
        Flight flight = sessionReq.getFlight();
        if (flight != null && flight.getDepartureTime() != null) {
            LocalDateTime cutoff = flight.getDepartureTime().minusHours(3);
            if (LocalDateTime.now().isAfter(cutoff)) {
                session.removeAttribute("bookingReq");
                redirectAttributes.addFlashAttribute("flightExpiredMsg",
                    "Phiên đặt vé đã hết hiệu lực. Chuyến bay " + flight.getFlightNumber() + " không còn nhận đặt chỗ vì gần giờ khởi hành.");
                return "redirect:/";
            }
        }

        
        sessionReq.setPassengers(reqForm.getPassengers());

        return "redirect:/booking/step2";
    }

    @GetMapping("/step2")
    public String step2(Model model, HttpSession session) {
        BookingRequestDTO sessionReq = (BookingRequestDTO) session.getAttribute("bookingReq");
        if (sessionReq == null) {
            return "redirect:/";
        }
        
        model.addAttribute("req", sessionReq);
        model.addAttribute("baggageOptions", baggageOptionRepository.findAll());
        model.addAttribute("promotions", promotionRepo.findAll());
        return "booking/step2";
    }

    @PostMapping("/step2")
    public String processStep2(@ModelAttribute("req") BookingRequestDTO reqForm, HttpSession session, Principal principal) {
        BookingRequestDTO sessionReq = (BookingRequestDTO) session.getAttribute("bookingReq");
        if (sessionReq == null) {
            return "redirect:/";
        }

        sessionReq.setSelectedBaggageId(reqForm.getSelectedBaggageId());
        sessionReq.setPromotionCode(reqForm.getPromotionCode());
        
        String username = (principal != null) ? principal.getName() : null;
        Booking savedBooking = bookingService.createBookingFromRequest(sessionReq, username);
        
        session.removeAttribute("bookingReq");
        
        return "redirect:/booking/step3?bookingId=" + savedBooking.getId();
    }

    @GetMapping("/step3")
    public String step3(@RequestParam("bookingId") Long bookingId, Model model) {
        Booking booking = bookingService.findById(bookingId);
        if (booking == null) return "redirect:/";
        model.addAttribute("booking", booking);
        return "booking/step3";
    }

    @PostMapping("/confirmPayment")
    public String confirmPayment(@RequestParam("bookingId") Long bookingId, @RequestParam("paymentMethod") String paymentMethod) {
        bookingService.createPaymentForBooking(bookingId, paymentMethod);
        
        Booking booking = bookingService.findById(bookingId);
        if (booking != null && booking.getUser() != null && booking.getUser().getEmail() != null) {
            emailService.sendBookingConfirmationEmail(booking, booking.getUser().getEmail());
        }

        
        if (booking != null) {
            java.util.List<com.example.quanlybanvemaybay.entity.User> staffList = userRepo.findAll().stream()
                .filter(u -> u.getRole() != null && (u.getRole().getRoleName().equals("ADMIN") || u.getRole().getRoleName().equals("STAFF")))
                .toList();
            
            String groupId = java.util.UUID.randomUUID().toString();
            for (com.example.quanlybanvemaybay.entity.User staff : staffList) {
                com.example.quanlybanvemaybay.entity.Notification notif = new com.example.quanlybanvemaybay.entity.Notification();
                notif.setUser(staff);
                notif.setTitle("Yêu cầu xử lý vé mới");
                notif.setMessage("Khách hàng vừa đặt vé mới (Mã Vé: #" + booking.getId() + "). Vui lòng kiểm tra và xử lý.");
                notif.setIsRead(false);
                notif.setCreatedAt(java.time.LocalDateTime.now());
                notif.setGroupId(groupId);
                notifRepo.save(notif);
            }
        }
        
        return "redirect:/booking/success?bookingId=" + bookingId;
    }

    @GetMapping("/success")
    public String success(@RequestParam("bookingId") Long bookingId, Model model) {
        Booking booking = bookingService.findById(bookingId);
        if (booking == null) return "redirect:/";
        model.addAttribute("booking", booking);
        return "booking/success";
    }
}
