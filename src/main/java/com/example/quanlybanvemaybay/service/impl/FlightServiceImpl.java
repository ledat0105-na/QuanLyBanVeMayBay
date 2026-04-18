package com.example.quanlybanvemaybay.service.impl;

import com.example.quanlybanvemaybay.entity.Flight;
import com.example.quanlybanvemaybay.repository.FlightRepository;
import com.example.quanlybanvemaybay.service.itf.FlightService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    public FlightServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
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
