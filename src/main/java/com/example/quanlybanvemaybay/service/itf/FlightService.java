package com.example.quanlybanvemaybay.service.itf;

import com.example.quanlybanvemaybay.entity.Flight;
import java.util.List;

public interface FlightService {
    List<Flight> findAll();
    Flight findById(Long id);
    void save(Flight flight);
    void cancelFlight(Long id);
}
