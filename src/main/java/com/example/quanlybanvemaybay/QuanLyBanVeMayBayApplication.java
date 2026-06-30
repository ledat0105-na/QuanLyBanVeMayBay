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
    public void initDatabaseChanges() {
        try {
            jdbcTemplate.execute("ALTER TABLE passengers DROP INDEX passport_number");
            System.out.println("✅ DROPPED UNIQUE INDEX passport_number on passengers");
        } catch (Exception e) {
            System.out.println("⚠️ Index passport_number might already be dropped or not exist.");
        }

        try {
            // Explicitly create booking_baggage table if Hibernate ddl-auto=update failed to create it
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS booking_baggage (" +
                    "booking_id bigint NOT NULL, " +
                    "baggage_id bigint NOT NULL, " +
                    "KEY FK_booking_baggage_baggage (baggage_id), " +
                    "KEY FK_booking_baggage_booking (booking_id), " +
                    "CONSTRAINT FK_booking_baggage_baggage FOREIGN KEY (baggage_id) REFERENCES baggage_options (id), " +
                    "CONSTRAINT FK_booking_baggage_booking FOREIGN KEY (booking_id) REFERENCES bookings (id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;");
            System.out.println("✅ CREATED TABLE booking_baggage if not exists");
        } catch (Exception e) {
            System.out.println("⚠️ Could not create booking_baggage table: " + e.getMessage());
        }
    }














}
