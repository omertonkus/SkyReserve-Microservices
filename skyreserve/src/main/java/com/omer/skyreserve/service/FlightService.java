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
    private final ObjectMapper objectMapper; // Config'den gelecek

    private static final String REDIS_KEY_PREFIX = "flight:";

    public Flight getFlightById(Long id) {
        String key = REDIS_KEY_PREFIX + id;

        try {
            // 1. Önce Redis'e sor (Cevap String gelecek)
            String jsonFlight = (String) redisTemplate.opsForValue().get(key);

            if (jsonFlight != null) {
                log.info(">>> READ FROM REDIS (Cache Hit): Flight ID {}", id);
                // String'i (JSON) tekrar Flight nesnesine çeviriyoruz
                return objectMapper.readValue(jsonFlight, Flight.class);
            }

            // 2. Redis'te yoksa DB'den al
            log.info(">>> READ FROM DB (Cache Miss): Flight ID {}", id);
            Flight flight = flightRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Not Found Flight!"));

            // 3. DB'den aldığını String'e çevirip Redis'e kaydet
            String flightAsString = objectMapper.writeValueAsString(flight);
            redisTemplate.opsForValue().set(key, flightAsString, 10, TimeUnit.MINUTES);

            return flight;

        } catch (Exception e) {
            log.error("Redis işlemi sırasında hata oluştu: ", e);
            // Redis hata verse bile sistem durmasın, DB'ye gitsin (Safe-fail)
            return flightRepository.findById(id).orElse(null);
        }
    }
}