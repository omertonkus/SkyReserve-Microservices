# ✈️ SkyReserve: High-Concurrency Flight Booking System

SkyReserve is a Spring Boot-based microservices-ready flight booking engine designed to handle high-traffic scenarios and prevent "Race Conditions" during seat reservations.

## 🚀 The Problem & Solution
In high-traffic systems (like airline or concert bookings), multiple users often try to book the last available seat simultaneously. Without proper handling, this leads to **Double Booking**.

**SkyReserve solves this using:**
- **Pessimistic Locking:** Database-level locking to ensure only one thread can modify a specific flight record at a time.
- **Event-Driven Architecture:** Decoupling booking logic from secondary actions (like notifications) using **Apache Kafka**.
- **Distributed Caching:** Optimizing read performance with **Redis**.

## 🛠 Tech Stack
- **Backend:** Java 17, Spring Boot 3.x
- **Database:** PostgreSQL (with Transactional Locking)
- **Messaging:** Apache Kafka
- **Caching:** Redis
- **Containerization:** Docker & Docker Compose
- **Testing:** JUnit 5 (Concurrency Stress Tests)

## 🏗 Architecture
1. **REST Controller:** Handles incoming booking requests.
2. **Service Layer:** Manages business logic and `@Transactional` boundaries.
3. **Pessimistic Lock:** Ensures data integrity during seat decrement.
4. **Kafka Producer:** Fires a `booking-events` message upon successful reservation.

## 🚦 How to Run
1. Clone the repository.
2. Ensure Docker is running.
3. Start infrastructure:
   ```bash
   docker-compose up -d
4. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run

## 🧪 Concurrency Test Results
The system includes a stress test (`testConcurrencyBooking`) that simulates 100 concurrent users trying to book 5 available seats.

**Result:**
- **Success Bookings:** 5
- **Failed Requests:** 95
- **Final DB State:** 0 seats remaining (No negative values, no data corruption).
