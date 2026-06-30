package com.example.quanlybanvemaybay.dto;

import com.example.quanlybanvemaybay.entity.Flight;
import com.example.quanlybanvemaybay.entity.Passenger;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Data
public class BookingRequestDTO {
    private Flight flight;
    private Flight returnFlight;
    private int numberOfPassengers;
    private int adults = 1;
    private int children = 0;
    private int infants = 0;
    private List<Passenger> passengers = new ArrayList<>();
    private Long selectedBaggageId;
    private String promotionCode;
    private BigDecimal totalAmount;
}
