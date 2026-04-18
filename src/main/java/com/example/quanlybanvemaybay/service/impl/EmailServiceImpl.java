package com.example.quanlybanvemaybay.service.impl;

import com.example.quanlybanvemaybay.entity.Booking;
import com.example.quanlybanvemaybay.service.itf.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public EmailServiceImpl(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    @Async
    public void sendBookingConfirmationEmail(Booking booking, String toEmail) {
        if (toEmail == null || toEmail.isEmpty()) return;

        try {
            Context context = new Context();
            context.setVariable("booking", booking);

            String process = templateEngine.process("email/booking-confirmation", context);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            
            helper.setText(process, true);
            helper.setTo(toEmail);
            helper.setSubject("Xác nhận Đặt Vé Thành Công - SkyTravel");
            helper.setFrom("tle723772@gmail.com");

            javaMailSender.send(mimeMessage);
            System.out.println("Email sent successfully to: " + toEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Gửi email thất bại tới: " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void sendOtpEmail(String email, String otp) {
        try {
            org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
            message.setFrom("tle723772@gmail.com");
            message.setTo(email);
            message.setSubject("Mã OTP xác thực - SkyTravel");
            message.setText("Mã OTP của bạn là: " + otp + "\nMã này có hiệu lực trong 5 phút. Vui lòng không chia sẻ mã này cho bất kỳ ai.");
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void sendPasswordResetByAdminEmail(String email, String username, String newPassword) {
        if (email == null || email.isEmpty()) return;
        try {
            org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
            message.setFrom("tle723772@gmail.com");
            message.setTo(email);
            message.setSubject("Mật khẩu mới của bạn - SkyTravel");
            message.setText("Xin chào " + username + ",\n\nQuản trị viên đã đặt lại mật khẩu cho tài khoản của bạn. Mật khẩu mới của bạn là: " + newPassword + "\n\nVui lòng đăng nhập và tiến hành đổi lại mật khẩu này ngay lập tức để đảm bảo an toàn.\n\nTrân trọng,\nĐội ngũ SkyTravel");
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
