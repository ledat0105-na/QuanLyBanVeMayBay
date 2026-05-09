package com.example.quanlybanvemaybay.service.itf;

import com.example.quanlybanvemaybay.dto.request.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);

    void sendPasswordResetOtp(String identifier);

    void resetPassword(String identifier, String otpCode, String newPassword);

    void checkUserExists(String identifier);
}
