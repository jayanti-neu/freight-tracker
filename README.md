# 🚚 Freight Tracking System (Java Spring Boot)

A real-time freight tracking system built with **Java Spring Boot** and **PostgreSQL**, designed for logistics visibility and optimized backend performance.
This project is a work in progress, aiming to provide a robust foundation for tracking shipments with features like CRUD operations, filtering, statistics, and real-time updates using WebSockets.

---

## 🧰 Tech Stack

* **Backend**: Java 17, Spring Boot (v3.5.3)
* **Database**: PostgreSQL
* **Build Tool**: Maven
* **ORM**: Spring Data JPA (Hibernate)
* **Real-time Messaging**: WebSocket (STOMP + SockJS)
* **Documentation**: Markdown
* **Dev Tools**: IntelliJ, Git, Postman

---

## ✨ Features Implemented (Phase 1 & 2)

* Create, Read, Update, Delete (CRUD) endpoints for Shipments
* Filter shipments by status and origin via query parameters
* Enum-based shipment status handling (`IN_TRANSIT`, `DELIVERED`, etc.)
* Auto-timestamped updates
* Error handling for missing records (needs to be done better)
* DTO usage for stats and summaries

    * `ShipmentStatsDTO` returns total shipments, per-status counts, and common origin
    * `ShipmentUpdateMessage` sends minimal real-time updates via WebSocket
* PostgreSQL configuration (via `application.properties`)
* Real-time WebSocket updates for shipment creation and status change
* Frontend-ready WebSocket messages (via STOMP endpoint)

---

## 📁 Project Structure

```
src/
├── config/                         # WebSocket config (message broker setup)
├── controller/                     # REST API endpoints
├── model/                          # Shipment entity & enums
├── repository/                     # JPA interfaces for DB access
├── dto/                            # Data transfer objects (stats, updates)
├── websocket/                      # Broadcaster logic for status updates
└── FreightTrackerApplication.java
```

---

## 🚀 API Endpoints (Phase 1 & 2)

| Method   | Endpoint                                            | Description             |
| -------- | --------------------------------------------------- | ----------------------- |
| `GET`    | `/api/shipments`                                    | Get all shipments       |
| `POST`   | `/api/shipments`                                    | Create a new shipment   |
| `GET`    | `/api/shipments/{id}`                               | Get shipment by ID      |
| `PUT`    | `/api/shipments/{id}`                               | Update shipment by ID   |
| `DELETE` | `/api/shipments/{id}`                               | Delete shipment by ID   |
| `GET`    | `/api/shipments/search?origin=NY&status=IN_TRANSIT` | Filter shipments        |
| `GET`    | `/api/shipments/stats`                              | Get shipment statistics |

---

## 🔮 WebSocket Real-Time Updates

WebSocket setup enables live shipment status updates pushed to the frontend.

* **Connect URL**: `/ws`
* **Subscribe to**: `/topic/shipments`
* **Broadcast Message DTO Format**:

```json
{
  "shipmentId": 12,
  "trackingNumber": "ABC123XYZ",
  "status": "IN_TRANSIT",
  "lastUpdatedTime": "2025-07-12T18:30:00"
}
```

Use tools like [WebSocket King](https://websocketking.com/) or browser STOMP client to listen to real-time updates.

**STOMP** is a simple text-based messaging protocol used over WebSocket to send structured messages, while **SockJS** ensures browser compatibility.

---

## 🧪 Example JSON Payload (POST /api/shipments)

```json
{
  "origin": "New York",
  "destination": "Chicago",
  "status": "IN_TRANSIT",
  "trackingNumber": "TRK123456",
  "carrier": "FedEx",
  "priority": "HIGH"
}
```

---

## ⚙️ Getting Started

### Clone the repo

```bash
git clone https://github.com/yourusername/freight-tracker.git
cd freight-tracker
```

### Set up PostgreSQL database

```sql
CREATE DATABASE freightdb;
```

### Configure your application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/freightdb
spring.datasource.username=your_user
spring.datasource.password=your_password
```

*Note: Never commit passwords. Use `application.properties.example` instead.*

### Run the app

```bash
./mvnw spring-boot:run
```

---

## 📌 Roadmap

* ✅ **Phase 1**: CRUD + Filtering + Stats
* ✅ **Phase 2**: WebSocket-based real-time updates 
* 📈 **Phase 3**: Advanced statistics, algorithms, and refactor to service layer
* 🧹 **Phase 4**: Unit & integration testing
* ☁️ **Phase 5**: AWS deployment, Docker, serverless

---

## 📚 License

MIT License. Open to contributions!
