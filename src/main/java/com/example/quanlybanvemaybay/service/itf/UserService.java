package com.example.quanlybanvemaybay.service.itf;

public interface UserService {
    boolean usernameExists(String username);
    boolean emailExists(String email);
    boolean emailOtpValid(String email, String otpCode);
    void resetPassword(String email, String otpCode, String newEncodedPassword);
    
    java.util.List<com.example.quanlybanvemaybay.entity.User> getAllUsers();
    void createStaff(com.example.quanlybanvemaybay.dto.request.CreateStaffRequest request);
    void toggleLockUser(Long userId);
    void resetUserPassword(Long userId, String newPassword);
    void updateUserInfo(String username, String fullName, String phone);
    void changePassword(String username, String oldPassword, String newPassword);
    com.example.quanlybanvemaybay.entity.User getUserById(Long userId);
}
