package com.omer.skyreserve.service;

import com.omer.skyreserve.model.Flight;
import com.omer.skyreserve.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper; // Jackson 3

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightService {

    private final FlightRepository flightRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String REDIS_KEY_PREFIX = "flight:";

    public Flight getFlightById(Long id) {
        String key = REDIS_KEY_PREFIX + id;

        try {
            // It asks Redis. (The response will be a String)
            String jsonFlight = (String) redisTemplate.opsForValue().get(key);

            if (jsonFlight != null) {
                log.info(">>> READ FROM REDIS (Cache Hit): Flight ID {}", id);

                // Converts the String (JSON) back into a Flight object.
                return objectMapper.readValue(jsonFlight, Flight.class);
            }

            // If it's not in Redis, it retrieves it from the database.
            log.info(">>> READ FROM DB (Cache Miss): Flight ID {}", id);
            Flight flight = flightRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Not Found Flight!"));

            // It converts the data it retrieves from the database into a String and saves it to Redis.
            String flightAsString = objectMapper.writeValueAsString(flight);
            redisTemplate.opsForValue().set(key, flightAsString, 10, TimeUnit.MINUTES);

            return flight;

        } catch (Exception e) {
            log.error("An error occurred during the Redis process.: ", e);

            // Even if Redis throws an error, the system doesn't stop; it goes to the database. (Safe-fail)
            return flightRepository.findById(id).orElse(null);
        }
    }
}