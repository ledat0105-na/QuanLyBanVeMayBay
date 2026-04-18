package com.example.quanlybanvemaybay.service.itf;

import com.example.quanlybanvemaybay.dto.request.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);

    void sendPasswordResetOtp(String email);

    void resetPassword(String email, String otpCode, String newPassword);
}
