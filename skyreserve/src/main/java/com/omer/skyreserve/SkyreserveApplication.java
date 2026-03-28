package com.omer.skyreserve;

import com.omer.skyreserve.model.Flight;
import com.omer.skyreserve.repository.FlightRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SkyreserveApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkyreserveApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(FlightRepository flightRepository){
        return args -> {
            if(flightRepository.count() == 0){
                Flight flight = new Flight();
                flight.setFlightNumber("TK2026");
                flight.setDeparture("Istanbul");
                flight.setDestination("London");
                flight.setAvailableSeats(5); // Test için az koltuk koyalım
                flight.setPrice(new java.math.BigDecimal("2500.00"));
                flightRepository.save(flight);
                System.out.println(">>> SkyReserve: Test uçuşu oluşturuldu (ID: 1)");

            }
        };
    }

}
