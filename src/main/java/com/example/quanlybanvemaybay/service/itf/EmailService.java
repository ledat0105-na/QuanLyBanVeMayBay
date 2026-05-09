package com.example.quanlybanvemaybay.service.itf;

import com.example.quanlybanvemaybay.entity.Booking;

public interface EmailService {
    void sendBookingConfirmationEmail(Booking booking, String toEmail);
    void sendOtpEmail(String email, String otp);
    void sendPasswordResetByAdminEmail(String email, String username, String newPassword);
    void sendBookingStatusUpdateEmail(Booking booking, String status);
    void sendPassengerUpdateEmail(Booking booking);
}
