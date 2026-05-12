package com.example.quanlybanvemaybay.task;

import com.example.quanlybanvemaybay.entity.Booking;
import com.example.quanlybanvemaybay.entity.Flight;
import com.example.quanlybanvemaybay.repository.BookingRepository;
import com.example.quanlybanvemaybay.repository.FlightRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BookingCleanupTask {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;

    public BookingCleanupTask(BookingRepository bookingRepository, FlightRepository flightRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
    }

    
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void autoCancelExpiredBookings() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(10);
        
        List<Booking> allBookings = bookingRepository.findAll();
        for (Booking booking : allBookings) {
            if ("WAITING_PAYMENT".equals(booking.getBookingStatus()) && 
                booking.getBookingDate() != null && 
                booking.getBookingDate().isBefore(cutoffTime)) {
                
                
                booking.setBookingStatus("CANCELLED");
                bookingRepository.save(booking);
                
                
                Flight flight = booking.getFlight();
                if (flight != null && flight.getAvailableSeats() != null) {
                    int restoredSeats = flight.getAvailableSeats() + booking.getPassengers().size();
                    flight.setAvailableSeats(restoredSeats);
                    flightRepository.save(flight);
                }
                
                System.out.println("Auto-cancelled expired booking: " + booking.getBookingCode());
            }
        }
    }
}
