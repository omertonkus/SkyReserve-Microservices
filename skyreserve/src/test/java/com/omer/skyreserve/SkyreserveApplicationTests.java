package com.omer.skyreserve;

import com.omer.skyreserve.dto.BookingRequest;
import com.omer.skyreserve.repository.FlightRepository;
import com.omer.skyreserve.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scala.reflect.runtime.SymbolTable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
class SkyreserveApplicationTests {

    @Autowired // Spring bu sınıftan otomatik nesne üretir
    private BookingService bookingService;

    @Autowired
    private FlightRepository flightRepository;

    @Test
    void testConcurrencyBooking() throws InterruptedException {
        int userCount = 100; // 100 kişi saldırıyor!
        ExecutorService executorService = Executors.newFixedThreadPool(userCount);
        CountDownLatch latch = new CountDownLatch(1); // Herkesin aynı anda başlaması için işaret fişeği

        AtomicInteger successCount = new AtomicInteger(); // çoklu işçilerin (threads) güvenle sayı artırabildiği özel bir sınıftır.
        AtomicInteger failCount = new AtomicInteger();

        for(int i=0; i < userCount; i++){
            String passengerName = "Passenger-" + i;
            executorService.execute(()->{
                try{
                    latch.await();
                    bookingService.bookFlight(createRequest(passengerName));
                    successCount.incrementAndGet(); // bilet alma başarılı olursa başarı sayacını 1 artır.
                } catch (Exception e){
                    failCount.incrementAndGet();
                }
            });
        }

        latch.countDown(); // İşaret fişeği patladı! Herkes aynı anda saldırıyor!
        Thread.sleep(5000); // Testin bitmesini bekle

        System.out.println("=====================");
        System.out.println("TEST RESULT: ");
        System.out.println("Success Booking: " + successCount.get());
        System.out.println("Fail (No Seat): " + failCount.get());
        System.out.println("Remaining Seats in the Database: " +
                flightRepository.findById(1L).get().getAvailableSeats());
        System.out.println("=====================");
    }

    private BookingRequest createRequest(String name){
        BookingRequest request = new BookingRequest();
        request.setFlightId(1L);
        request.setPassengerName(name);
        return request;
    }

}
