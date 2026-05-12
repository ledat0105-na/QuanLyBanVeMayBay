package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.entity.Flight;
import com.example.quanlybanvemaybay.repository.FlightRepository;
import com.example.quanlybanvemaybay.service.itf.AirlineService;
import com.example.quanlybanvemaybay.service.itf.AirportService;
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

    public FlightSearchController(FlightRepository flightRepository, AirportService airportService, AirlineService airlineService) {
        this.flightRepository = flightRepository;
        this.airportService = airportService;
        this.airlineService = airlineService;
    }

    @GetMapping("/search")
    public String search(@RequestParam("depId") Long depId,
                         @RequestParam("arrId") Long arrId,
                         @RequestParam("depDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate depDate,
                         @RequestParam(value = "passengers", defaultValue = "1") int passengers,
                         Model model) {

        List<Flight> flights = flightRepository.searchFlights(depId, arrId, depDate, passengers);

        
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
        model.addAttribute("passengers", passengers);

        return "flight/search-results";
    }
}
