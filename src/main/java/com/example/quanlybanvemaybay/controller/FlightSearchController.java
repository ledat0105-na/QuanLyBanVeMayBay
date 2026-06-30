package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.entity.Airport;
import com.example.quanlybanvemaybay.entity.Flight;
import com.example.quanlybanvemaybay.repository.FlightRepository;
import com.example.quanlybanvemaybay.service.itf.AirlineService;
import com.example.quanlybanvemaybay.service.itf.AirportService;
import com.example.quanlybanvemaybay.service.itf.ExternalFlightService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/flights")
public class FlightSearchController {

    private final FlightRepository flightRepository;
    private final AirportService airportService;
    private final AirlineService airlineService;
    private final ExternalFlightService externalFlightService;

    public FlightSearchController(FlightRepository flightRepository, 
                                  AirportService airportService, 
                                  AirlineService airlineService,
                                  ExternalFlightService externalFlightService) {
        this.flightRepository = flightRepository;
        this.airportService = airportService;
        this.airlineService = airlineService;
        this.externalFlightService = externalFlightService;
    }

    @GetMapping("/search")
    public String search(@RequestParam("depId") Long depId,
                         @RequestParam("arrId") Long arrId,
                         @RequestParam("depDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate depDate,
                         @RequestParam(value = "retDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate retDate,
                         @RequestParam(value = "tripType", required = false, defaultValue = "ONE_WAY") String tripType,
                         @RequestParam(value = "outboundFlightId", required = false) Long outboundFlightId,
                         @RequestParam(value = "passengers", defaultValue = "1") int passengers,
                         Model model) {

        Airport depAirport = airportService.findById(depId);
        Airport arrAirport = airportService.findById(arrId);
        
        List<Flight> flights;
        boolean isRoundTrip = "ROUND_TRIP".equals(tripType);
        boolean isSelectReturnPhase = isRoundTrip && outboundFlightId != null;

        if (isSelectReturnPhase) {
            // Phase 2: Return flight search (swapping departure and arrival, using return date)
            if (arrAirport != null && depAirport != null && retDate != null) {
                externalFlightService.generateAndSaveFlightsForRoute(arrAirport, depAirport, retDate);
            }
            flights = flightRepository.searchFlights(arrId, depId, retDate, passengers);
            
            if (outboundFlightId != null) {
                model.addAttribute("outboundFlight", flightRepository.findById(outboundFlightId).orElse(null));
            }
        } else {
            // Phase 1 (or One-way): Outbound flight search
            if (depAirport != null && arrAirport != null) {
                externalFlightService.generateAndSaveFlightsForRoute(depAirport, arrAirport, depDate);
            }
            flights = flightRepository.searchFlights(depId, arrId, depDate, passengers);
        }

        // Filter flights departing in more than 3 hours
        java.time.LocalDateTime cutoff3h = java.time.LocalDateTime.now().plusHours(3);
        flights = flights.stream()
                .filter(f -> f.getDepartureTime() != null && f.getDepartureTime().isAfter(cutoff3h))
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("flights", flights);
        model.addAttribute("airports", airportService.findAll());
        model.addAttribute("airlines", airlineService.findAll());
        model.addAttribute("depId", depId);
        model.addAttribute("arrId", arrId);
        model.addAttribute("depDate", depDate);
        model.addAttribute("retDate", retDate);
        model.addAttribute("tripType", tripType);
        model.addAttribute("outboundFlightId", outboundFlightId);
        model.addAttribute("passengers", passengers);
        model.addAttribute("isSelectReturnPhase", isSelectReturnPhase);

        return "flight/search-results";
    }
}
