package com.omer.skyreserve.repository;

import com.omer.skyreserve.model.Flight;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    // Bu metod çağrıldığında veritabanındaki ilgili satır kilitlenir.
    // İşlem bitene kadar başka hiç kimse bu satırı güncelleyemez!
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM Flight f WHERE f.id = :id")
    Optional<Flight> findByIdWithLock(Long id);
}
