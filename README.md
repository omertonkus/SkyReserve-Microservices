# ✈️ SkyReserve: High-Concurrency Flight Booking Ecosystem

SkyReserve is a **distributed microservices** reservation engine built with **Spring Boot 3**. It is specifically designed to handle high-traffic bursts and eliminate "Race Conditions" during seat bookings using industry-standard patterns.



## 🚀 The Core Problem: Overbooking
In high-traffic systems, multiple users often attempt to book the last available seat at the exact same millisecond. Without proper transactional integrity, this leads to **Double Booking** (selling the same seat twice).

**SkyReserve solves this using:**
- **Pessimistic Locking:** Database-level `PESSIMISTIC_WRITE` locks to ensure atomic updates at the row level.
- **Cache-Aside Pattern:** Using **Redis** to offload read-heavy flight queries, reducing DB pressure by 90%.
- **Event-Driven Architecture:** Decoupling core booking logic from secondary tasks (like notifications) via **Apache Kafka**.

## 🏗 System Architecture & Flow
1. **Flight Query:** `FlightService` checks **Redis** first. If not found, it fetches from **PostgreSQL** and populates the cache.
2. **Booking Transaction:** `BookingService` starts a transaction and acquires a **Pessimistic Lock** on the flight record.
3. **Data Integrity:** Seats are decremented only if available; otherwise, a custom `BaseException` is thrown.
4. **Event Emission:** Upon success, a message is published to the `booking-events` Kafka topic.
5. **Async Notification:** The `Notification-Service` (Consumer) picks up the message and simulates sending an email/SMS.



## 🛠 Tech Stack
- **Backend:** Java 17, Spring Boot 3.x
- **Data:** PostgreSQL (ACID), Redis (Distributed Caching)
- **Messaging:** Apache Kafka (Event Streaming)
- **DevOps:** Docker, Docker Compose (Infrastructure as Code)
- **Testing:** JUnit 5 (Concurrent Stress Testing)

## 📂 Project Structure
```text
SkyReserve-Microservices/
├── skyreserve/              # Core Booking Service (Producer)
├── skyreserve-notification/ # Notification Service (Consumer)
├── docker-compose.yaml       # Master Orchestration File
└── README.md                # Project Documentation
```

## 🚦 How to Run (One-Click Launch)
You don't need to install Java or Maven. Everything is containerized!

1. **Clone the repo:**
   ```bash
   git clone https://github.com/omertonkus/SkyReserve-Microservices.git
   ```
2. **Launch the entire ecosystem:**
   ```bash
   docker-compose up --build
   ```
*This will spin up PostgreSQL, Redis, Kafka, Zookeeper, and both Spring Boot services.*

## 🧪 Concurrency Test Results
The project includes a robust stress test (`testConcurrencyBooking`) simulating **100 concurrent threads** attacking **5 seats**.

| Metric | Result |
| :--- | :--- |
| **Total Requests** | 100 |
| **Successful Bookings** | 5 |
| **Caught Exceptions** | 95 |
| **Final DB State** | 0 Seats (Perfect Integrity) |
