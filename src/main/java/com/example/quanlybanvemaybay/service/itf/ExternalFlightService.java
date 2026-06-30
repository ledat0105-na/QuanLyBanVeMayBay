package com.example.quanlybanvemaybay.service.itf;

import com.example.quanlybanvemaybay.entity.Airport;
import java.time.LocalDate;

public interface ExternalFlightService {
    void generateAndSaveFlightsForRoute(Airport departureAirport, Airport arrivalAirport, LocalDate date);
}
