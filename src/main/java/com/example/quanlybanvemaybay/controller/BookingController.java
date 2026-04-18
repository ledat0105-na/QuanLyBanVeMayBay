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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/booking")
@SessionAttributes("bookingReq")
public class BookingController {

    private final FlightService flightService;
    private final BaggageOptionRepository baggageOptionRepository;
    private final BookingService bookingService;
    private final EmailService emailService;

    public BookingController(FlightService flightService, BaggageOptionRepository baggageOptionRepository, BookingService bookingService, EmailService emailService) {
        this.flightService = flightService;
        this.baggageOptionRepository = baggageOptionRepository;
        this.bookingService = bookingService;
        this.emailService = emailService;
    }

    @GetMapping("/step1")
    public String step1(@RequestParam("flightId") Long flightId, 
                        @RequestParam(value = "passengers", defaultValue = "1") int numPassengers, 
                        Model model, HttpSession session) {
        
        Flight flight = flightService.findById(flightId);
        if (flight == null) {
            return "redirect:/";
        }

        BookingRequestDTO req = new BookingRequestDTO();
        req.setFlight(flight);
        req.setNumberOfPassengers(numPassengers);
        
        for (int i = 0; i < numPassengers; i++) {
            req.getPassengers().add(new Passenger());
        }

        session.setAttribute("bookingReq", req);
        model.addAttribute("req", req);
        
        return "booking/step1";
    }

    @PostMapping("/step1")
    public String processStep1(@ModelAttribute("req") BookingRequestDTO reqForm, HttpSession session) {
        BookingRequestDTO sessionReq = (BookingRequestDTO) session.getAttribute("bookingReq");
        if (sessionReq == null) {
            return "redirect:/";
        }
        
        // Update passengers
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
