package com.omer.skyreservenotification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationListener {

    @KafkaListener(topics = "booking-events", groupId = "notification-group")
    public void listenBookingEvents(String message){
        log.info("--------------------------");
        log.info("MESSAGE RECEIVED FROM KAFKA: {}", message);
        log.info("ACTION: Email/SMS is being sent to the customer...");
        log.info("STATUS: Notification successfully delivered.");
        log.info("--------------------------");
    }
}
