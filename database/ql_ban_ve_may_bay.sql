-- ======================================================
-- DATABASE: AIRLINE BOOKING SYSTEM
-- ======================================================

DROP DATABASE IF EXISTS airline_booking_system;
CREATE DATABASE airline_booking_system;
USE airline_booking_system;

-- ======================================================
-- 1. ROLES (Phân quyền hệ thống)
-- ======================================================

CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(20)
);

INSERT INTO roles(role_name) VALUES
('ADMIN'),
('STAFF'),
('USER');

-- ======================================================
-- 2. USERS (Tài khoản hệ thống)
-- Đăng ký bằng email hoặc số điện thoại
-- ======================================================

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(255),
    full_name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    role_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

INSERT INTO users(username,password,full_name,email,phone,role_id) VALUES
('admin','123456','System Admin','admin@gmail.com','0900000001',1),
('staff1','123456','Staff Booking','staff1@gmail.com','0900000002',2),
('user1','123456','Nguyen Van A','user1@gmail.com','0900000003',3),
('user2','123456','Tran Van B','user2@gmail.com','0900000004',3),
('user3','123456','Le Van C','user3@gmail.com','0900000005',3),
('user4','123456','Pham Van D','user4@gmail.com','0900000006',3),
('user5','123456','Hoang Van E','user5@gmail.com','0900000007',3),
('user6','123456','Vo Van F','user6@gmail.com','0900000008',3),
('user7','123456','Dang Van G','user7@gmail.com','0900000009',3),
('user8','123456','Bui Van H','user8@gmail.com','0900000010',3);

-- ======================================================
-- 3. EMAIL OTP (Xác thực email khi đăng ký)
-- ======================================================

CREATE TABLE email_otp (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100),
    otp_code VARCHAR(10),
    expire_time DATETIME,
    is_used BOOLEAN DEFAULT FALSE
);

-- ======================================================
-- 4. AIRLINES (Hãng hàng không)
-- ======================================================

CREATE TABLE airlines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    code VARCHAR(10)
);

INSERT INTO airlines(name,code) VALUES
('Vietnam Airlines','VN'),
('VietJet Air','VJ'),
('Bamboo Airways','QH'),
('Pacific Airlines','BL'),
('AirAsia','AK'),
('Thai Airways','TG'),
('Singapore Airlines','SQ'),
('Korean Air','KE'),
('Japan Airlines','JL'),
('Emirates','EK');

-- ======================================================
-- 5. AIRPORTS (Sân bay)
-- ======================================================

CREATE TABLE airports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10),
    name VARCHAR(100),
    city VARCHAR(100),
    country VARCHAR(100)
);

INSERT INTO airports(code,name,city,country) VALUES
('SGN','Tan Son Nhat Airport','Ho Chi Minh','Vietnam'),
('HAN','Noi Bai Airport','Ha Noi','Vietnam'),
('DAD','Da Nang Airport','Da Nang','Vietnam'),
('CXR','Cam Ranh Airport','Nha Trang','Vietnam'),
('PQC','Phu Quoc Airport','Phu Quoc','Vietnam'),
('BKK','Suvarnabhumi Airport','Bangkok','Thailand'),
('SIN','Changi Airport','Singapore','Singapore'),
('ICN','Incheon Airport','Seoul','Korea'),
('NRT','Narita Airport','Tokyo','Japan'),
('DXB','Dubai Airport','Dubai','UAE');

-- ======================================================
-- 6. AIRCRAFT (Máy bay)
-- ======================================================

CREATE TABLE aircraft (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    airline_id BIGINT,
    model VARCHAR(100),
    total_seats INT,
    FOREIGN KEY (airline_id) REFERENCES airlines(id)
);

INSERT INTO aircraft(airline_id,model,total_seats) VALUES
(1,'Airbus A320',180),
(1,'Boeing 787',250),
(2,'Airbus A321',200),
(3,'Boeing 737',190),
(4,'Airbus A320',180),
(5,'Airbus A320',180),
(6,'Boeing 777',300),
(7,'Airbus A350',280),
(8,'Boeing 747',350),
(9,'Boeing 767',220);

-- ======================================================
-- 7. FLIGHTS (Chuyến bay)
-- ======================================================

CREATE TABLE flights (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(20),
    airline_id BIGINT,
    departure_airport_id BIGINT,
    arrival_airport_id BIGINT,
    departure_time DATETIME,
    arrival_time DATETIME,
    base_price DECIMAL(10,2),

    FOREIGN KEY (airline_id) REFERENCES airlines(id),
    FOREIGN KEY (departure_airport_id) REFERENCES airports(id),
    FOREIGN KEY (arrival_airport_id) REFERENCES airports(id)
);

INSERT INTO flights VALUES
(1,'VN123',1,1,2,'2026-04-01 08:00:00','2026-04-01 10:00:00',1500000),
(2,'VN456',1,2,3,'2026-04-02 09:00:00','2026-04-02 11:00:00',1200000),
(3,'VJ111',2,1,3,'2026-04-03 07:00:00','2026-04-03 09:00:00',1100000),
(4,'QH222',3,3,1,'2026-04-04 10:00:00','2026-04-04 12:00:00',1300000),
(5,'BL333',4,2,1,'2026-04-05 13:00:00','2026-04-05 15:00:00',1250000),
(6,'AK444',5,1,6,'2026-04-06 09:00:00','2026-04-06 13:00:00',3000000),
(7,'TG555',6,6,2,'2026-04-07 14:00:00','2026-04-07 17:00:00',3200000),
(8,'SQ666',7,7,1,'2026-04-08 08:00:00','2026-04-08 11:00:00',3500000),
(9,'KE777',8,8,2,'2026-04-09 10:00:00','2026-04-09 14:00:00',4500000),
(10,'JL888',9,9,1,'2026-04-10 11:00:00','2026-04-10 15:00:00',5000000);

-- ======================================================
-- 8. PROMOTIONS (Mã giảm giá)
-- ======================================================

CREATE TABLE promotions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50),
    discount_percent INT,
    start_date DATE,
    end_date DATE
);

INSERT INTO promotions(code,discount_percent,start_date,end_date) VALUES
('FLY10',10,'2026-01-01','2026-12-31'),
('FLY20',20,'2026-01-01','2026-12-31'),
('NEWUSER',15,'2026-01-01','2026-12-31'),
('SUMMER',25,'2026-06-01','2026-08-31'),
('HOLIDAY',30,'2026-12-01','2026-12-31');

-- ======================================================
-- 9. BOOKINGS (Đặt vé)
-- ======================================================

CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_code VARCHAR(20),
    user_id BIGINT,
    flight_id BIGINT,
    total_amount DECIMAL(10,2),
    booking_status VARCHAR(50),
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (flight_id) REFERENCES flights(id)
);

INSERT INTO bookings(booking_code,user_id,flight_id,total_amount,booking_status) VALUES
('BK001',3,1,1500000,'CONFIRMED'),
('BK002',4,2,1200000,'CONFIRMED'),
('BK003',5,3,1100000,'PENDING'),
('BK004',6,4,1300000,'CONFIRMED'),
('BK005',7,5,1250000,'CANCELLED');

-- ======================================================
-- 10. PASSENGERS (Hành khách)
-- ======================================================

CREATE TABLE passengers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT,
    full_name VARCHAR(100),
    gender VARCHAR(10),
    date_of_birth DATE,
    passport_number VARCHAR(50),

    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

-- ======================================================
-- 11. BAGGAGE OPTIONS (Hành lý)
-- ======================================================

CREATE TABLE baggage_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    weight INT,
    price DECIMAL(10,2)
);

INSERT INTO baggage_options(name,weight,price) VALUES
('10kg baggage',10,200000),
('20kg baggage',20,400000),
('30kg baggage',30,600000);

-- ======================================================
-- 12. PAYMENTS (Thanh toán)
-- ======================================================

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT,
    amount DECIMAL(10,2),
    payment_method VARCHAR(50),
    transaction_code VARCHAR(100),
    payment_status VARCHAR(50),

    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

INSERT INTO payments(booking_id,amount,payment_method,transaction_code,payment_status) VALUES
(1,1500000,'BANK_TRANSFER','TXN001','SUCCESS'),
(2,1200000,'VNPAY','TXN002','SUCCESS'),
(3,1100000,'MOMO','TXN003','PENDING'),
(4,1300000,'BANK_TRANSFER','TXN004','SUCCESS'),
(5,1250000,'VNPAY','TXN005','FAILED');

-- ======================================================
-- 13. TICKETS (Vé điện tử)
-- ======================================================

CREATE TABLE tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT,
    ticket_number VARCHAR(50),
    issued_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    qr_code VARCHAR(255),
    email_sent BOOLEAN DEFAULT FALSE,

    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

-- ======================================================
-- 14. EMAIL LOGS (Lịch sử gửi mail)
-- ======================================================

CREATE TABLE email_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    email VARCHAR(100),
    subject VARCHAR(255),
    content TEXT,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id)
);