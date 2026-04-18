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
    public void sendPasswordResetOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));

        String otpCode = generateOtpCode();
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(10);

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
    public void resetPassword(String email, String otpCode, String newPassword) {
        EmailOtp otp = emailOtpRepository
                .findFirstByEmailAndOtpCodeAndIsUsedFalseOrderByExpireTimeDesc(email, otpCode)
                .orElseThrow(() -> new IllegalArgumentException("OTP không hợp lệ hoặc đã hết hạn"));

        if (otp.getExpireTime() == null || otp.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP đã hết hạn");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy email trong hệ thống"));

        otp.setIsUsed(true);
        emailOtpRepository.save(otp);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private String generateOtpCode() {
        int otp = secureRandom.nextInt(1_000_000);
        return String.format("%06d", otp);
    }
}
