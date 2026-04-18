package com.example.quanlybanvemaybay.service.itf;

import com.example.quanlybanvemaybay.entity.Airline;

import java.util.List;

public interface AirlineService {
    List<Airline> findAll();

    Airline findById(Long id);

    void save(Airline airline);

    void delete(Long id);
}
