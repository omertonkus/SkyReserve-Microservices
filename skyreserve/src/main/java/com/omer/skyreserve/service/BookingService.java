package com.omer.skyreserve.service;

import com.omer.skyreserve.dto.BookingRequest;
import com.omer.skyreserve.exception.BaseException;
import com.omer.skyreserve.model.Flight;
import com.omer.skyreserve.repository.FlightRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingService {

    private final FlightRepository flightRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    public BookingService(FlightRepository flightRepository, KafkaTemplate kafkaTemplate, RedisTemplate redisTemplate){
        this.flightRepository = flightRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Transactional // rollback
    public String bookFlight(BookingRequest request){

        // Veritabanındaki satırı KİLİTLE (Pessimistic Lock)
        Flight flight = flightRepository.findByIdWithLock(request.getFlightId())
                .orElseThrow(()-> new BaseException("Flight Not Found! ID: "+ request.getFlightId()));

        // Koltuk kontrolü
        if(flight.getAvailableSeats() <= 0){
            throw new BaseException("Sorry, flight number " + flight.getFlightNumber() + " is fully booked!");
        }

        // Koltuk sayısını azalt ve kaydet
        flight.setAvailableSeats(flight.getAvailableSeats() - 1);
        flightRepository.save(flight);

        // Bilet satıldıktan hemen sonra Redis'teki eski bilgiyi sil (Cache Eviction)
        String redisKey = "flight:" + request.getFlightId();
        redisTemplate.delete(redisKey);
        log.info(">>> REDIS TEMİZLENDİ: Güncel veri bir sonraki sorguda DB'den gelecek.");

        // Kafka'ya anons geç (Asenkron bildirim)
        String message = String.format("TICKET SOLD: Passenger %s purchased a ticket for flight %s.",
                request.getPassengerName(), flight.getFlightNumber());
        kafkaTemplate.send("booking-events", message);

        return "Your ticket has been successfully reserved! Remaining seat:" + flight.getAvailableSeats();

    }

}
