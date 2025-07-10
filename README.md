# 🚚 Freight Tracking System (Java Spring Boot)

A real-time freight tracking system built with **Java Spring Boot** and **PostgreSQL**, designed for logistics visibility and optimized backend performance.
This project is a work in progress, aiming to provide a robust foundation for tracking shipments with features like CRUD operations, filtering, and statistics.

---

## 🧰 Tech Stack

- **Backend**: Java 17, Spring Boot (v3.5.3)
- **Database**: PostgreSQL
- **Build Tool**: Maven
- **ORM**: Spring Data JPA (Hibernate)
- **Testing**: Spring Boot Test
- **Documentation**: Markdown + Postman
- **Dev Tools**: IntelliJ, Git, Postman

---

## ✨ Features Implemented (Phase 1)

- Create, Read, Update, Delete (CRUD) endpoints for Shipments
- Filter shipments by status and origin via query parameters
- Enum-based shipment status handling (`IN_TRANSIT`, `DELIVERED`, etc.)
- Auto-timestamped updates
- Error handling for missing records
- DTO usage for stats and summaries
- PostgreSQL configuration (via `application.properties`)

---

## 📁 Project Structure

```
src/
├── controller/                     # REST API endpoints
├── model/                         # Shipment entity & enums
├── repository/                    # JPA interfaces for DB access
├── dto/                          # Data transfer objects (stats, summaries)
└── FreightTrackerApplication.java
```

---

## 🚀 API Endpoints (Phase 1)

| Method   | Endpoint                                                      | Description                        |
|----------|---------------------------------------------------------------|------------------------------------|
| `GET`    | `/api/shipments`                                             | Get all shipments                  |
| `POST`   | `/api/shipments`                                             | Create a new shipment              |
| `GET`    | `/api/shipments/{id}`                                        | Get shipment by ID                 |
| `PUT`    | `/api/shipments/{id}`                                        | Update shipment by ID              |
| `DELETE` | `/api/shipments/{id}`                                        | Delete shipment by ID              |
| `GET`    | `/api/shipments/search?origin=NY&status=IN_TRANSIT`        | Filter shipments                   |
| `GET`    | `/api/shipments/stats`                                       | Get shipment statistics            |
| `GET`    | `/api/shipments/summary`                                     | Get summary insights               |

---

## 🧪 Example JSON Payload (POST /api/shipments)

```json
{
  "origin": "New York",
  "destination": "Chicago",
  "status": "IN_TRANSIT",
  "trackingNumber": "TRK123456"
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

### Run the app

```bash
./mvnw spring-boot:run
```

---

## 📌 Roadmap

- ✅ **Phase 1**: CRUD + Filtering + Stats
- 🔄 **Phase 2**: WebSocket-based real-time updates
- 📊 **Phase 3**: Advanced statistics & algorithms
- 🧪 **Phase 4**: Unit & integration testing
- ☁️ **Phase 5**: AWS deployment, Docker, serverless

---

## 📚 License

MIT License. Open to contributions!