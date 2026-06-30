package com.example.quanlybanvemaybay;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class QuanLyBanVeMayBayApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuanLyBanVeMayBayApplication.class, args);
    }

    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void dropPassportIndex() {
        try {
            jdbcTemplate.execute("ALTER TABLE passengers DROP INDEX passport_number");
            System.out.println("✅ DROPPED UNIQUE INDEX passport_number on passengers");
        } catch (Exception e) {
            System.out.println("⚠️ Index passport_number might already be dropped or not exist.");
        }
    }














}
