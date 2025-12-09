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

## Steps To Run Application
1) Ensure docker is installed
2) Ensure jar of respective applications are present in the target file
3) Run ```mvn package``` in every service to ensure latest image gets build
4) run docker compose up in root directory

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
| POST   | /api/auth/signup | Register a new user in AuthService |
| POST   | /api/auth/signin | Authenticate a user and get JWT |

---
## Eureka Dashboard:
<img width="1919" height="889" alt="Image" src="https://github.com/user-attachments/assets/debd87e6-9d6c-4df1-8e6a-f23e68eba355" />

---
## Docker Containers
<img width="1919" height="820" alt="Image" src="https://github.com/user-attachments/assets/0b2be17e-046c-4051-ab29-741a6d5d452c" />
(note: docker logs can be found in main root directory)
---

## Reports:
### SonarQube Report
(note: sonarqube link for each service has been provided in REPORT.docx)
<img width="1381" height="699" alt="Image" src="https://github.com/user-attachments/assets/bbe008f8-d9e5-4f7a-8aac-417af8d3b952" />
---

### Jacoco Report
<img width="1569" height="395" alt="Image" src="https://github.com/user-attachments/assets/793849c2-d7cc-4362-b588-796152634ad4" />
<img width="1562" height="294" alt="Image" src="https://github.com/user-attachments/assets/76cb542d-31e5-4757-af1b-b87b884d9139" />
<img width="1542" height="342" alt="Image" src="https://github.com/user-attachments/assets/b4361319-cd5b-4cc9-8f3a-ad7fd1384db7" />
<img width="1511" height="417" alt="Image" src="https://github.com/user-attachments/assets/2dc4dcb2-161c-450a-82df-2be9de37f3da" />

---
### Jmeter Report
(note: cli testing mode can be found in REPORT.docx)
#### 20 Request
<img width="1450" height="291" alt="Image" src="https://github.com/user-attachments/assets/6fc089bc-59bf-4e4b-9c9a-3e3f89c2d68f" />

#### 50 Request
<img width="1447" height="293" alt="Image" src="https://github.com/user-attachments/assets/be8aebb3-b085-4469-a8a4-19fafda676f8" />

#### 100 Request
<img width="1455" height="296" alt="Image" src="https://github.com/user-attachments/assets/378e8cdf-4f32-4993-900a-6a264fbbe2d9" />

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
