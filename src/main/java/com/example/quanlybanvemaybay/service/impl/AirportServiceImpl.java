package com.example.quanlybanvemaybay.service.impl;

import com.example.quanlybanvemaybay.entity.Airport;
import com.example.quanlybanvemaybay.repository.AirportRepository;
import com.example.quanlybanvemaybay.service.itf.AirportService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirportServiceImpl implements AirportService {

    private final AirportRepository airportRepository;

    public AirportServiceImpl(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @Override
    public List<Airport> findAll() {
        return airportRepository.findAll();
    }

    @Override
    public Airport findById(Long id) {
        return airportRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Airport airport) {
        airportRepository.save(airport);
    }

    @Override
    public void delete(Long id) {
        airportRepository.deleteById(id);
    }
}
