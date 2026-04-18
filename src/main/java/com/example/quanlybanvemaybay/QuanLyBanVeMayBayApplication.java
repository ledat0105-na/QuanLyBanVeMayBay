package com.example.quanlybanvemaybay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class QuanLyBanVeMayBayApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuanLyBanVeMayBayApplication.class, args);
    }
//    @org.springframework.context.annotation.Bean
//    public org.springframework.boot.CommandLineRunner printBcryptPassword() {
//        return args -> {
//            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
//            String rawPass = "123456";
//            String encodedPass = encoder.encode(rawPass);
//            System.out.println("==========================================================");
//            System.out.println("🔑 PASSWORD CHO TẤT CẢ TÀI KHOẢN (123456) ĐÃ MÀ HÓA BCRYPT LÀ:");
//            System.out.println(encodedPass);
//            System.out.println("👉 COPY ĐOẠN MÃ TRÊN BỎ VÀO CHỖ 'PASSWORD' TRONG DATABASE");
//            System.out.println("==========================================================");
//        };
//    }

}
