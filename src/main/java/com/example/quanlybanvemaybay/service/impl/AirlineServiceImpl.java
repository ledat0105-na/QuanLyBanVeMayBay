package com.example.quanlybanvemaybay.service.impl;

import com.example.quanlybanvemaybay.entity.Airline;
import com.example.quanlybanvemaybay.repository.AirlineRepository;
import com.example.quanlybanvemaybay.service.itf.AirlineService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirlineServiceImpl implements AirlineService {

    private final AirlineRepository airlineRepository;

    public AirlineServiceImpl(AirlineRepository airlineRepository) {
        this.airlineRepository = airlineRepository;
    }

    @Override
    public List<Airline> findAll() {
        return airlineRepository.findAll();
    }

    @Override
    public Airline findById(Long id) {
        return airlineRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Airline airline) {
        airlineRepository.save(airline);
    }

    @Override
    public void delete(Long id) {
        airlineRepository.deleteById(id);
    }
}
