package com.example.quanlybanvemaybay.service.impl;

import com.example.quanlybanvemaybay.entity.Airline;
import com.example.quanlybanvemaybay.entity.Airport;
import com.example.quanlybanvemaybay.entity.Flight;
import com.example.quanlybanvemaybay.repository.AirlineRepository;
import com.example.quanlybanvemaybay.repository.FlightRepository;
import com.example.quanlybanvemaybay.service.itf.ExternalFlightService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExternalFlightServiceImpl implements ExternalFlightService {

    private final AirlineRepository airlineRepository;
    private final FlightRepository flightRepository;

    public ExternalFlightServiceImpl(AirlineRepository airlineRepository, FlightRepository flightRepository) {
        this.airlineRepository = airlineRepository;
        this.flightRepository = flightRepository;
    }

    @Override
    @Transactional
    public void generateAndSaveFlightsForRoute(Airport departureAirport, Airport arrivalAirport, LocalDate date) {
        // 1. Get or create the 4 Vietnamese airlines
        Airline vn = getOrCreateAirline("VN", "Vietnam Airlines");
        Airline vj = getOrCreateAirline("VJ", "VietJet Air");
        Airline qh = getOrCreateAirline("QH", "Bamboo Airways");
        Airline vu = getOrCreateAirline("VU", "Vietravel Airlines");

        // 2. Fetch existing flights to prevent duplicates
        List<Flight> allFlights = flightRepository.findAll();

        // If there are already flights on this day and route, do not generate anything
        boolean hasExistingFlightsOnRouteAndDay = allFlights.stream().anyMatch(f ->
                f.getDepartureAirport() != null && f.getArrivalAirport() != null &&
                f.getDepartureAirport().getId().equals(departureAirport.getId()) &&
                f.getArrivalAirport().getId().equals(arrivalAirport.getId()) &&
                f.getDepartureTime() != null && f.getDepartureTime().toLocalDate().isEqual(date)
        );

        if (hasExistingFlightsOnRouteAndDay) {
            return;
        }

        // 3. Define flight templates to generate
        List<FlightTemplate> templates = new ArrayList<>();
        
        // Vietnam Airlines templates
        templates.add(new FlightTemplate(vn, "VN-172", LocalTime.of(7, 30), 135, BigDecimal.valueOf(2150000)));
        templates.add(new FlightTemplate(vn, "VN-186", LocalTime.of(16, 0), 135, BigDecimal.valueOf(1950000)));

        // VietJet Air templates
        templates.add(new FlightTemplate(vj, "VJ-244", LocalTime.of(9, 0), 135, BigDecimal.valueOf(1150000)));
        templates.add(new FlightTemplate(vj, "VJ-258", LocalTime.of(21, 15), 135, BigDecimal.valueOf(980000)));

        // Bamboo Airways templates
        templates.add(new FlightTemplate(qh, "QH-202", LocalTime.of(11, 30), 135, BigDecimal.valueOf(1450000)));
        templates.add(new FlightTemplate(qh, "QH-218", LocalTime.of(18, 30), 135, BigDecimal.valueOf(1320000)));

        // Vietravel Airlines templates
        templates.add(new FlightTemplate(vu, "VU-137", LocalTime.of(13, 45), 135, BigDecimal.valueOf(890000)));

        for (FlightTemplate temp : templates) {
            String cleanPrefix = temp.flightNumberPrefix.replace("-", "");
            String depLetter = departureAirport.getCode().substring(0, 1);
            String arrLetter = arrivalAirport.getCode().substring(0, 1);
            String dateSuffix = date.toString().substring(5).replace("-", "");
            String routeFlightNum = cleanPrefix + depLetter + arrLetter + dateSuffix;
            LocalDateTime departureTime = LocalDateTime.of(date, temp.departureTime);
            LocalDateTime arrivalTime = departureTime.plusMinutes(temp.durationMinutes);

            // Check if this flight already exists
            boolean exists = allFlights.stream().anyMatch(f -> 
                f.getFlightNumber().equalsIgnoreCase(routeFlightNum) && 
                f.getDepartureTime().isEqual(departureTime) &&
                f.getDepartureAirport().getId().equals(departureAirport.getId()) &&
                f.getArrivalAirport().getId().equals(arrivalAirport.getId())
            );

            if (!exists) {
                Flight newFlight = Flight.builder()
                        .flightNumber(routeFlightNum)
                        .airline(temp.airline)
                        .departureAirport(departureAirport)
                        .arrivalAirport(arrivalAirport)
                        .departureTime(departureTime)
                        .arrivalTime(arrivalTime)
                        .basePrice(temp.price)
                        .totalSeats(180)
                        .availableSeats(180)
                        .isCancelled(false)
                        .build();

                flightRepository.save(newFlight);
            }
        }
    }

    private Airline getOrCreateAirline(String code, String name) {
        Optional<Airline> optionalAirline = airlineRepository.findByCode(code);
        if (optionalAirline.isPresent()) {
            return optionalAirline.get();
        }
        Airline airline = Airline.builder()
                .code(code)
                .name(name)
                .build();
        return airlineRepository.save(airline);
    }

    private static class FlightTemplate {
        Airline airline;
        String flightNumberPrefix;
        LocalTime departureTime;
        int durationMinutes;
        BigDecimal price;

        FlightTemplate(Airline airline, String flightNumberPrefix, LocalTime departureTime, int durationMinutes, BigDecimal price) {
            this.airline = airline;
            this.flightNumberPrefix = flightNumberPrefix;
            this.departureTime = departureTime;
            this.durationMinutes = durationMinutes;
            this.price = price;
        }
    }
}
