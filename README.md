# Flight Booking Microservice

## Architecture & System Features

- **Real-time email notifications** triggered during booking, cancellation, and addition of flights in inventory
- **Microservice architecture** with independently deployable services and isolated databases
- **Service Registry (Eureka)** for automatic service discovery
- **API Gateway** to route external requests and provide a single unified entry point
- **Config Server** for centralized configuration management
- **Circuit Breaker (Resilience4j)** to prevent cascading failures and improve resilience
- **Kafka event streaming** for asynchronous, reliable, and decoupled inter-service communication
- **Redis caching** to improve response times, reduce database load, and store frequently accessed data such as flight availability and session information
- **Docker containerization** for packaging each microservice with its dependencies, enabling consistent deployment and easy orchestration
- **Independent scalability** allowing each service to scale based on its own load
---

## Architecture Diagram
<img width="1745" height="943" alt="Image" src="https://github.com/user-attachments/assets/1d45a939-1907-4252-80d4-3b999f6764ab" />

---
## FlightService Database ER Diagram
<img width="906" height="537" alt="Image" src="https://github.com/user-attachments/assets/0ec4f9a0-8d44-48fc-b5f4-823ecf421427" />

---

## BookingService Database ER Diagram
<img width="824" height="292" alt="Image" src="https://github.com/user-attachments/assets/77f28b3d-c683-44f0-ba6c-23b36ca36542" />

---

## API Endpoints

| Method | Endpoint | Description |
|--------|---------|-------------|
| POST   | /api/flight/airline/inventory | Add inventory/schedule for an existing airline |
| POST   | /api/flight/search | Search for available flights |
| POST   | /api/flight/booking/{flightId} | Book a ticket for a flight |
| GET    | /api/flight/ticket/{pnr} | Get booked ticket details using PNR |
| GET    | /api/flight/booking/history/{emailId} | Get booked ticket history by email ID |
| DELETE | /api/flight/booking/cancel/{pnr} | Cancel a booked ticket using PNR |


---
## Eureka Dashboard:
<img width="1920" height="928" alt="Image" src="https://github.com/user-attachments/assets/ef965681-a369-45c4-b1f8-8fd2edc6f19c" />

---

---
## Reports:
### SonarQube Report
(note: sonarqube link for each service has been provided in REPORT.docx)
<img width="1383" height="224" alt="Image" src="https://github.com/user-attachments/assets/50268fd8-baef-4ef2-aeec-430fc8faf75c" />
<img width="1379" height="225" alt="Image" src="https://github.com/user-attachments/assets/2478c71a-7a83-499d-a20c-097cde820405" />
<img width="1389" height="223" alt="Image" src="https://github.com/user-attachments/assets/d0aa2b71-dfff-4c29-8516-5ec0a3b053c4" />

---

### Jacoco Report
<img width="1657" height="328" alt="Image" src="https://github.com/user-attachments/assets/f9e088ad-6908-4961-9fb3-fb22b8569bcb" />
<img width="1602" height="307" alt="Image" src="https://github.com/user-attachments/assets/0528d409-9fa4-4237-9bc9-3d234dedb429" />
<img width="1541" height="272" alt="Image" src="https://github.com/user-attachments/assets/1c994f76-d239-4f25-9c99-70893fe05134" />

---
### Jmeter Report
(note: cli testing mode can be found in REPORT.docx)
#### 20 Request
<img width="1920" height="503" alt="Image" src="https://github.com/user-attachments/assets/bf0851db-2f03-464d-af9d-193b20ccb39d" />

#### 50 Request
<img width="1919" height="507" alt="Image" src="https://github.com/user-attachments/assets/c32b9384-4c58-4b5a-a132-2e3b4efd5369" />

#### 100 Request
<img width="1920" height="592" alt="Image" src="https://github.com/user-attachments/assets/df1f3028-6f50-41b2-8a2f-29f0f3ba3208" />

---
### Newman
newman report can be found in newman_report folder and REPORT.docx

### Postman
postman images and sample api request and response can be found in REPORT.docx

## Email Service
### Ticket booking
#### Email:
<img width="1347" height="634" alt="Image" src="https://github.com/user-attachments/assets/fbbe6ef2-a1fb-4e36-bec7-cd4c76ac7444" />

#### Kafka Topic:
<img width="1787" height="504" alt="Image" src="https://github.com/user-attachments/assets/ad12662a-c672-48f9-8c1a-7681303b63e2" />

### Ticket cancellation
#### Email:
<img width="1371" height="291" alt="Image" src="https://github.com/user-attachments/assets/fb3f7f9f-94c5-46b6-afbe-4c6ac4c395f2" />

#### Kafka Topic:
<img width="1789" height="564" alt="Image" src="https://github.com/user-attachments/assets/ebb9687a-67da-41d9-98fe-3c8351af236a" />

### Flight added to inventory
### Email:
<img width="1314" height="424" alt="Image" src="https://github.com/user-attachments/assets/3c0114c9-093e-4ba4-a03b-d87e52d995b9" />

### Kafka Topic:
<img width="1787" height="269" alt="Image" src="https://github.com/user-attachments/assets/63f9c3ee-b1e9-4415-b304-557aa8025d9f" />
