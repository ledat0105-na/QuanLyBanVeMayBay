-- ======================================================
-- DATABASE: AIRLINE BOOKING SYSTEM
-- MySQL Script Full + Sample Data
-- ======================================================

DROP DATABASE IF EXISTS airline_booking_system;
CREATE DATABASE airline_booking_system
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE airline_booking_system;

-- ======================================================
-- 1. ROLES
-- ======================================================

CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(20) NOT NULL UNIQUE
);

INSERT INTO roles (role_name) VALUES
('ADMIN'),
('STAFF'),
('USER');

-- ======================================================
-- 2. USERS
-- ======================================================

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    role_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role
        FOREIGN KEY (role_id) REFERENCES roles(id)
);

INSERT INTO users (username, password, full_name, email, phone, role_id) VALUES
('admin','123456','System Admin','admin@gmail.com','0900000001',1),
('staff1','123456','Staff Booking 1','staff1@gmail.com','0900000002',2),
('staff2','123456','Staff Booking 2','staff2@gmail.com','0900000003',2),
('user1','123456','Nguyen Van A','user1@gmail.com','0900000004',3),
('user2','123456','Tran Van B','user2@gmail.com','0900000005',3),
('user3','123456','Le Van C','user3@gmail.com','0900000006',3),
('user4','123456','Pham Van D','user4@gmail.com','0900000007',3),
('user5','123456','Hoang Van E','user5@gmail.com','0900000008',3),
('user6','123456','Vo Van F','user6@gmail.com','0900000009',3),
('user7','123456','Dang Van G','user7@gmail.com','0900000010',3);

-- ======================================================
-- 3. EMAIL OTP
-- ======================================================

CREATE TABLE email_otp (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    otp_code VARCHAR(10) NOT NULL,
    expire_time DATETIME NOT NULL,
    is_used BOOLEAN DEFAULT FALSE
);

INSERT INTO email_otp (email, otp_code, expire_time, is_used) VALUES
('user1@gmail.com','123456','2026-03-01 10:00:00',TRUE),
('user2@gmail.com','234567','2026-03-01 10:05:00',FALSE),
('user3@gmail.com','345678','2026-03-01 10:10:00',TRUE),
('user4@gmail.com','456789','2026-03-01 10:15:00',FALSE),
('user5@gmail.com','567890','2026-03-01 10:20:00',TRUE),
('user6@gmail.com','678901','2026-03-01 10:25:00',FALSE),
('user7@gmail.com','789012','2026-03-01 10:30:00',TRUE),
('staff1@gmail.com','890123','2026-03-01 10:35:00',TRUE),
('staff2@gmail.com','901234','2026-03-01 10:40:00',FALSE),
('admin@gmail.com','112233','2026-03-01 10:45:00',TRUE);

-- ======================================================
-- 4. AIRLINES
-- ======================================================

CREATE TABLE airlines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(10) NOT NULL UNIQUE
);

INSERT INTO airlines (name, code) VALUES
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
-- 5. AIRPORTS
-- ======================================================

CREATE TABLE airports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL
);

INSERT INTO airports (code, name, city, country) VALUES
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
-- 6. AIRCRAFT
-- ======================================================

CREATE TABLE aircraft (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    airline_id BIGINT NOT NULL,
    model VARCHAR(100) NOT NULL,
    total_seats INT NOT NULL,
    CONSTRAINT fk_aircraft_airline
        FOREIGN KEY (airline_id) REFERENCES airlines(id)
);

INSERT INTO aircraft (airline_id, model, total_seats) VALUES
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
-- 7. FLIGHTS
-- ======================================================

CREATE TABLE flights (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(20) NOT NULL UNIQUE,
    airline_id BIGINT NOT NULL,
    departure_airport_id BIGINT NOT NULL,
    arrival_airport_id BIGINT NOT NULL,
    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_flights_airline
        FOREIGN KEY (airline_id) REFERENCES airlines(id),
    CONSTRAINT fk_flights_departure_airport
        FOREIGN KEY (departure_airport_id) REFERENCES airports(id),
    CONSTRAINT fk_flights_arrival_airport
        FOREIGN KEY (arrival_airport_id) REFERENCES airports(id)
);

INSERT INTO flights
(flight_number, airline_id, departure_airport_id, arrival_airport_id, departure_time, arrival_time, base_price)
VALUES
('VN123',1,1,2,'2026-04-01 08:00:00','2026-04-01 10:00:00',1500000),
('VN456',1,2,3,'2026-04-02 09:00:00','2026-04-02 11:00:00',1200000),
('VJ111',2,1,3,'2026-04-03 07:00:00','2026-04-03 09:00:00',1100000),
('QH222',3,3,1,'2026-04-04 10:00:00','2026-04-04 12:00:00',1300000),
('BL333',4,2,1,'2026-04-05 13:00:00','2026-04-05 15:00:00',1250000),
('AK444',5,1,6,'2026-04-06 09:00:00','2026-04-06 13:00:00',3000000),
('TG555',6,6,2,'2026-04-07 14:00:00','2026-04-07 17:00:00',3200000),
('SQ666',7,7,1,'2026-04-08 08:00:00','2026-04-08 11:00:00',3500000),
('KE777',8,8,2,'2026-04-09 10:00:00','2026-04-09 14:00:00',4500000),
('JL888',9,9,1,'2026-04-10 11:00:00','2026-04-10 15:00:00',5000000);

-- ======================================================
-- 8. PROMOTIONS
-- ======================================================

CREATE TABLE promotions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    discount_percent INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL
);

INSERT INTO promotions (code, discount_percent, start_date, end_date) VALUES
('FLY10',10,'2026-01-01','2026-12-31'),
('FLY20',20,'2026-01-01','2026-12-31'),
('NEWUSER',15,'2026-01-01','2026-12-31'),
('SUMMER',25,'2026-06-01','2026-08-31'),
('HOLIDAY',30,'2026-12-01','2026-12-31'),
('SPRING5',5,'2026-02-01','2026-03-31'),
('VIP35',35,'2026-01-01','2026-12-31'),
('WEEKEND12',12,'2026-01-01','2026-12-31'),
('NIGHT15',15,'2026-01-01','2026-12-31'),
('FLASH50',50,'2026-07-01','2026-07-05');

-- ======================================================
-- 9. BOOKINGS
-- ======================================================

CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_code VARCHAR(20) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    flight_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    booking_status VARCHAR(50) NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_bookings_flight
        FOREIGN KEY (flight_id) REFERENCES flights(id)
);

INSERT INTO bookings (booking_code, user_id, flight_id, total_amount, booking_status) VALUES
('BK001',4,1,1500000,'CONFIRMED'),
('BK002',5,2,1200000,'CONFIRMED'),
('BK003',6,3,1100000,'PENDING'),
('BK004',7,4,1300000,'CONFIRMED'),
('BK005',8,5,1250000,'CANCELLED'),
('BK006',9,6,3000000,'CONFIRMED'),
('BK007',10,7,3200000,'PENDING'),
('BK008',4,8,3500000,'CONFIRMED'),
('BK009',5,9,4500000,'CONFIRMED'),
('BK010',6,10,5000000,'PENDING');

-- ======================================================
-- 10. PASSENGERS
-- ======================================================

CREATE TABLE passengers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    gender VARCHAR(10),
    date_of_birth DATE,
    passport_number VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT fk_passengers_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

INSERT INTO passengers (booking_id, full_name, gender, date_of_birth, passport_number) VALUES
(1,'Nguyen Van A','Male','1998-01-10','P000001'),
(2,'Tran Van B','Male','1997-02-11','P000002'),
(3,'Le Thi C','Female','2000-03-12','P000003'),
(4,'Pham Thi D','Female','1999-04-13','P000004'),
(5,'Hoang Van E','Male','1996-05-14','P000005'),
(6,'Vo Thi F','Female','1995-06-15','P000006'),
(7,'Dang Van G','Male','1994-07-16','P000007'),
(8,'Bui Thi H','Female','1993-08-17','P000008'),
(9,'Do Van I','Male','1992-09-18','P000009'),
(10,'Ngo Thi K','Female','1991-10-19','P000010');

-- ======================================================
-- 11. BAGGAGE OPTIONS
-- ======================================================

CREATE TABLE baggage_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    weight INT NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

INSERT INTO baggage_options (name, weight, price) VALUES
('10kg baggage',10,200000),
('15kg baggage',15,300000),
('20kg baggage',20,400000),
('25kg baggage',25,500000),
('30kg baggage',30,600000),
('35kg baggage',35,700000),
('40kg baggage',40,800000),
('45kg baggage',45,900000),
('50kg baggage',50,1000000),
('60kg baggage',60,1200000);

-- ======================================================
-- 12. PAYMENTS
-- ======================================================

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    transaction_code VARCHAR(100) NOT NULL UNIQUE,
    payment_status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_payments_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

INSERT INTO payments (booking_id, amount, payment_method, transaction_code, payment_status) VALUES
(1,1500000,'BANK_TRANSFER','TXN001','SUCCESS'),
(2,1200000,'VNPAY','TXN002','SUCCESS'),
(3,1100000,'MOMO','TXN003','PENDING'),
(4,1300000,'BANK_TRANSFER','TXN004','SUCCESS'),
(5,1250000,'VNPAY','TXN005','FAILED'),
(6,3000000,'MOMO','TXN006','SUCCESS'),
(7,3200000,'BANK_TRANSFER','TXN007','PENDING'),
(8,3500000,'VNPAY','TXN008','SUCCESS'),
(9,4500000,'MOMO','TXN009','SUCCESS'),
(10,5000000,'BANK_TRANSFER','TXN010','FAILED');

-- ======================================================
-- 13. TICKETS
-- ======================================================

CREATE TABLE tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    ticket_number VARCHAR(50) NOT NULL UNIQUE,
    issued_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    qr_code VARCHAR(255),
    email_sent BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_tickets_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

INSERT INTO tickets (booking_id, ticket_number, qr_code, email_sent) VALUES
(1,'TK001','QR001',TRUE),
(2,'TK002','QR002',TRUE),
(3,'TK003','QR003',FALSE),
(4,'TK004','QR004',TRUE),
(5,'TK005','QR005',FALSE),
(6,'TK006','QR006',TRUE),
(7,'TK007','QR007',FALSE),
(8,'TK008','QR008',TRUE),
(9,'TK009','QR009',TRUE),
(10,'TK010','QR010',FALSE);

-- ======================================================
-- 14. EMAIL LOGS
-- ======================================================

CREATE TABLE email_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    email VARCHAR(100) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    content TEXT,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_email_logs_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

INSERT INTO email_logs (user_id, email, subject, content) VALUES
(4,'user1@gmail.com','Xac nhan dat ve BK001','Ve cua ban da duoc xac nhan'),
(5,'user2@gmail.com','Xac nhan dat ve BK002','Ve cua ban da duoc xac nhan'),
(6,'user3@gmail.com','Thong bao thanh toan BK003','Vui long hoan tat thanh toan'),
(7,'user4@gmail.com','Xac nhan dat ve BK004','Ve cua ban da duoc xac nhan'),
(8,'user5@gmail.com','Thong bao huy BK005','Booking cua ban da bi huy'),
(9,'user6@gmail.com','Xac nhan dat ve BK006','Ve cua ban da duoc xac nhan'),
(10,'user7@gmail.com','Thong bao thanh toan BK007','Vui long hoan tat thanh toan'),
(4,'user1@gmail.com','Gui ve dien tu TK001','Ve dien tu da duoc gui'),
(5,'user2@gmail.com','Gui ve dien tu TK002','Ve dien tu da duoc gui'),
(6,'user3@gmail.com','Gui ve dien tu TK003','Ve dien tu dang cho xu ly');

-- ======================================================
-- CHECK DATA
-- ======================================================

SELECT * FROM roles;
SELECT * FROM users;
SELECT * FROM email_otp;
SELECT * FROM airlines;
SELECT * FROM airports;
SELECT * FROM aircraft;
SELECT * FROM flights;
SELECT * FROM promotions;
SELECT * FROM bookings;
SELECT * FROM passengers;
SELECT * FROM baggage_options;
SELECT * FROM payments;
SELECT * FROM tickets;
SELECT * FROM email_logs;
