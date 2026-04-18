package com.example.quanlybanvemaybay.service.itf;

import com.example.quanlybanvemaybay.entity.Airport;

import java.util.List;

public interface AirportService {
    List<Airport> findAll();

    Airport findById(Long id);

    void save(Airport airport);

    void delete(Long id);
}
