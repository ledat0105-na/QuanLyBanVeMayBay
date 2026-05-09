package com.example.quanlybanvemaybay.service.impl;

import com.example.quanlybanvemaybay.entity.EmailOtp;
import com.example.quanlybanvemaybay.entity.User;
import com.example.quanlybanvemaybay.repository.EmailOtpRepository;
import com.example.quanlybanvemaybay.repository.UserRepository;
import com.example.quanlybanvemaybay.repository.RoleRepository;
import com.example.quanlybanvemaybay.service.itf.UserService;
import com.example.quanlybanvemaybay.entity.Role;
import com.example.quanlybanvemaybay.dto.request.CreateStaffRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailOtpRepository emailOtpRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, 
                           EmailOtpRepository emailOtpRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailOtpRepository = emailOtpRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean emailOtpValid(String email, String otpCode) {
        return emailOtpRepository.findFirstByEmailAndOtpCodeAndIsUsedFalseOrderByExpireTimeDesc(email, otpCode)
                .map(otp -> otp.getExpireTime() != null && otp.getExpireTime().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    @Override
    public void resetPassword(String email, String otpCode, String newEncodedPassword) {
        EmailOtp otp = emailOtpRepository.findFirstByEmailAndOtpCodeAndIsUsedFalseOrderByExpireTimeDesc(email, otpCode)
                .orElseThrow(() -> new IllegalArgumentException("OTP không hợp lệ hoặc đã hết hạn"));

        if (otp.getExpireTime() == null || !otp.getExpireTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP đã hết hạn");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy email trong hệ thống"));

        otp.setIsUsed(true);
        emailOtpRepository.save(otp);

        user.setPassword(newEncodedPassword);
        userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void createStaff(CreateStaffRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        Role staffRole = roleRepository.findByRoleName("STAFF")
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy role STAFF trong bảng roles"));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(staffRole)
                .createdAt(LocalDateTime.now())
                .isLocked(false)
                .build();

        userRepository.save(user);
    }

    @Override
    public void toggleLockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        
        Boolean currentStatus = user.getIsLocked() != null ? user.getIsLocked() : false;
        user.setIsLocked(!currentStatus);
        userRepository.save(user);
    }

    @Override
    public void resetUserPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void updateUserInfo(String username, String fullName, String phone) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        user.setFullName(fullName);
        user.setPhone(phone);
        userRepository.save(user);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
    }

    @Override
    public void updateUserAdmin(Long userId, String fullName, String email, String phone) {
        User user = getUserById(userId);
        if (!user.getEmail().equals(email) && emailExists(email)) {
            throw new IllegalArgumentException("Email đã được sử dụng bởi người dùng khác");
        }
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        userRepository.save(user);
    }
}
