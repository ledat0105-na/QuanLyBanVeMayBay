package com.example.quanlybanvemaybay.service.impl;

import com.example.quanlybanvemaybay.dto.BookingRequestDTO;
import com.example.quanlybanvemaybay.entity.*;
import com.example.quanlybanvemaybay.repository.*;
import com.example.quanlybanvemaybay.service.itf.BookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final BaggageOptionRepository baggageOptionRepository;
    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, PassengerRepository passengerRepository, 
                              BaggageOptionRepository baggageOptionRepository, PromotionRepository promotionRepository, 
                              UserRepository userRepository, PaymentRepository paymentRepository,
                              NotificationRepository notificationRepository) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.baggageOptionRepository = baggageOptionRepository;
        this.promotionRepository = promotionRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.notificationRepository = notificationRepository;
    }

    private void sendNotif(User user, String title, String message) {
        if (user == null) return;
        Notification notif = new Notification();
        notif.setUser(user);
        notif.setTitle(title);
        notif.setMessage(message);
        notif.setIsRead(false);
        notif.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notif);
    }

    @Override
    @Transactional
    public Booking createBookingFromRequest(BookingRequestDTO req, String username) {
        User user = null;
        if (username != null) {
            user = userRepository.findByUsername(username).orElse(null);
        }
        
        BigDecimal totalAmount = req.getFlight().getBasePrice().multiply(BigDecimal.valueOf(req.getNumberOfPassengers()));
        
        Booking booking = new Booking();
        booking.setBookingCode("BKG" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        booking.setUser(user);
        booking.setFlight(req.getFlight());
        booking.setBookingDate(LocalDateTime.now());
        booking.setBookingStatus("WAITING_PAYMENT");
        
        if (req.getSelectedBaggageId() != null) {
            BaggageOption baggage = baggageOptionRepository.findById(req.getSelectedBaggageId()).orElse(null);
            if (baggage != null) {
                booking.getBaggageOptions().add(baggage);
                totalAmount = totalAmount.add(baggage.getPrice());
            }
        }
        
        if (req.getPromotionCode() != null && !req.getPromotionCode().isEmpty()) {
            Promotion promo = promotionRepository.findByCode(req.getPromotionCode());
            LocalDate today = LocalDate.now();
            if (promo != null && promo.getIsActive() && 
                promo.getStartDate().compareTo(today) <= 0 && promo.getEndDate().compareTo(today) >= 0) {
                
                BigDecimal discount = totalAmount.multiply(BigDecimal.valueOf(promo.getDiscountPercent())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                totalAmount = totalAmount.subtract(discount);
                
                // Track usage
                if (promo.getUsageLimit() != null && promo.getUsageLimit() > 0) {
                    promo.setUsageLimit(promo.getUsageLimit() - 1);
                    promotionRepository.save(promo);
                }
            }
        }
        
        booking.setTotalAmount(totalAmount);
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Decrement available seats
        Flight flight = req.getFlight();
        if (flight.getAvailableSeats() != null) {
            int newAvailable = flight.getAvailableSeats() - req.getNumberOfPassengers();
            flight.setAvailableSeats(Math.max(0, newAvailable));
            // Let JPA handle dirty checking since flight is likely in persistence context
            // or we might need flightRepository if not attached
        }
        
        if (req.getPassengers() != null) {
            for (Passenger p : req.getPassengers()) {
                p.setBooking(savedBooking);
                passengerRepository.save(p);
            }
        }

        // Send notifications
        if (user != null) {
            // Notif to Customer
            sendNotif(user, "Đặt vé thành công", "Mã đặt vé của bạn là: " + savedBooking.getBookingCode() + ". Vui lòng thanh toán để hoàn tất.");
            
            // Notif to Admin/Staff
            List<User> adminsAndStaff = userRepository.findAll().stream()
                .filter(u -> u.getRole() != null && (u.getRole().getRoleName().equals("ADMIN") || u.getRole().getRoleName().equals("STAFF")))
                .toList();
            for (User admin : adminsAndStaff) {
                sendNotif(admin, "Có đơn đặt vé mới", "Khách hàng " + user.getFullName() + " vừa đặt vé: " + savedBooking.getBookingCode());
            }
        }
        
        return savedBooking;
    }

    @Override
    public Booking findById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void createPaymentForBooking(Long bookingId, String paymentMethod) {
        Booking booking = findById(bookingId);
        if (booking != null) {
            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setAmount(booking.getTotalAmount());
            payment.setPaymentMethod(paymentMethod);
            payment.setPaymentStatus("PENDING");
            // Auto generate transaction code placeholder for manual entry later if desired
            payment.setTransactionCode("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            paymentRepository.save(payment);

            // Notify Admin about payment request
            if (booking.getUser() != null) {
                List<User> adminsAndStaff = userRepository.findAll().stream()
                    .filter(u -> u.getRole() != null && (u.getRole().getRoleName().equals("ADMIN") || u.getRole().getRoleName().equals("STAFF")))
                    .toList();
                for (User admin : adminsAndStaff) {
                    sendNotif(admin, "Yêu cầu thanh toán", "Khách hàng " + booking.getUser().getFullName() + " vừa thực hiện thanh toán cho đơn: " + booking.getBookingCode());
                }
            }
        }
    }

    @Override
    public java.util.List<Booking> getBookingsByUsername(String username) {
        return bookingRepository.findByUserUsernameOrderByBookingDateDesc(username);
    }

    @Override
    public java.util.List<Booking> getAllBookings() {
        return bookingRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "bookingDate"));
    }

    @Override
    @Transactional
    public Booking updateBookingStatus(Long id, String status) {
        Booking booking = findById(id);
        if (booking != null) {
            String oldStatus = booking.getBookingStatus();
            booking.setBookingStatus(status);
            bookingRepository.save(booking);

            // Notify Customer about status update
            if (booking.getUser() != null && !status.equals(oldStatus)) {
                String msg = "Trạng thái đơn đặt vé " + booking.getBookingCode() + " đã thay đổi thành: " + status;
                sendNotif(booking.getUser(), "Cập nhật trạng thái vé", msg);
            }
        }
        return booking;
    }

    @Override
    @Transactional
    public void updatePassengerInfo(Long passengerId, String fullName, String gender, LocalDate doB, String passportNumber) {
        Passenger passenger = passengerRepository.findById(passengerId).orElse(null);
        if (passenger != null) {
            passenger.setFullName(fullName);
            passenger.setGender(gender);
            passenger.setDateOfBirth(doB);
            passenger.setPassportNumber(passportNumber);
            passengerRepository.save(passenger);
        }
    }
}
