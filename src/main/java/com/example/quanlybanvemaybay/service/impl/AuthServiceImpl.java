package com.example.quanlybanvemaybay.service.impl;

import com.example.quanlybanvemaybay.dto.request.RegisterRequest;
import com.example.quanlybanvemaybay.entity.EmailOtp;
import com.example.quanlybanvemaybay.entity.Role;
import com.example.quanlybanvemaybay.entity.User;
import com.example.quanlybanvemaybay.repository.EmailOtpRepository;
import com.example.quanlybanvemaybay.repository.RoleRepository;
import com.example.quanlybanvemaybay.repository.UserRepository;
import com.example.quanlybanvemaybay.service.itf.AuthService;
import com.example.quanlybanvemaybay.service.itf.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailOtpRepository emailOtpRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthServiceImpl(UserRepository userRepository,
                            RoleRepository roleRepository,
                            EmailOtpRepository emailOtpRepository,
                            EmailService emailService,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.emailOtpRepository = emailOtpRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy role USER trong bảng roles"));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(userRole)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }

    @Override
    public void sendPasswordResetOtp(String identifier) {
        User user = userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản với thông tin này"));

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Tài khoản này chưa cập nhật email để nhận mã OTP");
        }

        String otpCode = generateOtpCode();
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(1);

        EmailOtp otp = EmailOtp.builder()
                .email(user.getEmail())
                .otpCode(otpCode)
                .expireTime(expireTime)
                .isUsed(false)
                .build();
        emailOtpRepository.save(otp);

        emailService.sendOtpEmail(user.getEmail(), otpCode);
    }

    @Override
    public void resetPassword(String identifier, String otpCode, String newPassword) {
        User user = userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));

        EmailOtp otp = emailOtpRepository
                .findFirstByEmailAndOtpCodeAndIsUsedFalseOrderByExpireTimeDesc(user.getEmail(), otpCode)
                .orElseThrow(() -> new IllegalArgumentException("OTP không hợp lệ hoặc đã hết hạn"));

        if (otp.getExpireTime() == null || otp.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP đã hết hạn");
        }

        otp.setIsUsed(true);
        emailOtpRepository.save(otp);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void checkUserExists(String identifier) {
        User user = userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản với thông tin này"));

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Tài khoản này chưa cập nhật email để nhận mã OTP");
        }
    }

    private String generateOtpCode() {
        int otp = secureRandom.nextInt(1_000_000);
        return String.format("%06d", otp);
    }
}
