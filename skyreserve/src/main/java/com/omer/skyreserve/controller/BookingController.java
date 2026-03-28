package com.omer.skyreserve.controller;

import com.omer.skyreserve.dto.BookingRequest;
import com.omer.skyreserve.service.BookingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService){
        this.bookingService= bookingService;
    }

    @PostMapping
    public String createBooking(@RequestBody BookingRequest request){
        return bookingService.bookFlight(request);
    }
}
