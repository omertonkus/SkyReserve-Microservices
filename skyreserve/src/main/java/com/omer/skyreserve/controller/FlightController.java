package com.omer.skyreserve.controller;

import com.omer.skyreserve.model.Flight;
import com.omer.skyreserve.service.BookingService;
import com.omer.skyreserve.service.FlightService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService){
        this.flightService = flightService;
    }

    @GetMapping("/{id}")
    public Flight getFlights(@PathVariable Long id){
        return flightService.getFlightById(id);
    }

}
