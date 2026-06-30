package com.example.quanlybanvemaybay.service.itf;

import com.example.quanlybanvemaybay.dto.BookingRequestDTO;
import com.example.quanlybanvemaybay.entity.Booking;

public interface BookingService {
    Booking createBookingFromRequest(BookingRequestDTO requestDTO, String username);
    Booking findById(Long id);
    void createPaymentForBooking(Long bookingId, String paymentMethod);
    java.util.List<Booking> getBookingsByUsername(String username);
    
    
    java.util.List<Booking> getAllBookings();
    Booking updateBookingStatus(Long id, String status);
    void updatePassengerInfo(Long passengerId, String fullName, String gender, java.time.LocalDate doB, String passportNumber);
    void checkInBooking(Long id);
}
