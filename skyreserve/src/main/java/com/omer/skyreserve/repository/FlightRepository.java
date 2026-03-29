package com.omer.skyreserve.repository;

import com.omer.skyreserve.model.Flight;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    // When this method is called, the corresponding row in the database is locked.
    // This row cannot be updated until the operation is complete!
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM Flight f WHERE f.id = :id")
    Optional<Flight> findByIdWithLock(Long id);
}
