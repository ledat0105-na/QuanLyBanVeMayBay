package com.example.quanlybanvemaybay.service.impl;

import com.example.quanlybanvemaybay.entity.Flight;
import com.example.quanlybanvemaybay.repository.FlightRepository;
import com.example.quanlybanvemaybay.service.itf.FlightService;
import org.springframework.stereotype.Service;

import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.annotation.PostConstruct;

import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final JdbcTemplate jdbcTemplate;

    public FlightServiceImpl(FlightRepository flightRepository, JdbcTemplate jdbcTemplate) {
        this.flightRepository = flightRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void fixDatabaseSchema() {
        try {
            jdbcTemplate.execute("ALTER TABLE flights MODIFY aircraft_id BIGINT NULL");
        } catch (Exception e) {
            
        }
        try {
            jdbcTemplate.execute("ALTER TABLE flights MODIFY base_price DECIMAL(10,2) NOT NULL");
        } catch (Exception e) {
            
        }
    }

    @Override
    public List<Flight> findAll() {
        return flightRepository.findAll();
    }

    @Override
    public Flight findById(Long id) {
        return flightRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Flight flight) {
        if (flight.getTotalSeats() != null && flight.getAvailableSeats() == null) {
            flight.setAvailableSeats(flight.getTotalSeats());
        }
        if (flight.getIsCancelled() == null) {
            flight.setIsCancelled(false);
        }
        flightRepository.save(flight);
    }

    @Override
    public void cancelFlight(Long id) {
        Flight flight = findById(id);
        if (flight != null) {
            flight.setIsCancelled(true);
            flightRepository.save(flight);
        }
    }
}
