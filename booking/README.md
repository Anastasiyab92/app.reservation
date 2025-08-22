# Restaurant Booking System

A Spring Boot application for managing restaurant table reservations with integration to external CRM and Gastro systems.

## Features

- ✅ Table availability checking
- ✅ Reservation creation and management
- ✅ Integration with CRM and Gastro systems
- ✅ RESTful API endpoints
- ✅ Input validation and error handling
- ✅ Comprehensive logging
- ✅ Database connection pooling
- ✅ Environment-specific configurations

## Technologies

- **Spring Boot 3.5.5**
- **Spring Data JPA**
- **MySQL Database** (Production)
- **H2 Database** (Testing)
- **Maven**
- **Lombok**
- **OpenAPI/Swagger**
- **JUnit 5** for testing

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Setup Instructions

### 1. Database Setup

Create MySQL database:
```sql
CREATE DATABASE restaurant_db;
```

### 2. Environment Variables

Set the database password:
```bash
export DB_PASSWORD=your_secure_password
```

### 3. Build and Run

```bash
# Build the project
./mvnw clean compile

# Run in development mode
./mvnw spring-boot:run -Dspring.profiles.active=dev

# Run in production mode
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

### 4. Access the Application

- **API**: http://localhost:8080/api/reservations
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console** (dev only): http://localhost:8080/h2-console

## API Endpoints

### Get Available Tables
```http
GET /api/reservations/available?date=2025-08-25&time=12:00&numberOfGuests=4
```

### Create Reservation
```http
POST /api/reservations
Content-Type: application/json

{
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "phoneNumber": "1234567890"
  },
  "table": {
    "id": 1,
    "number": 1,
    "capacity": 4
  },
  "date": "2025-08-15",
  "time": "12:00",
  "numberOfGuests": 4,
  "status": "BOOKED"
}
```

### Get All Reservations
```http
GET /api/reservations
```

## Testing

```bash
# Run all tests
./mvnw test

# Run tests with coverage
./mvnw test jacoco:report

# Run integration tests only
./mvnw test -Dtest=*IntegrationTest
```

## Configuration

### Development Profile
- SQL logging enabled
- Debug logging
- H2 console enabled
- Detailed error messages

### Production Profile
- SQL logging disabled
- Warning level logging
- H2 console disabled
- Minimal error details

## Security Notes

- Database password is configured via environment variable `DB_PASSWORD`
- Input validation is enabled on all endpoints
- Global exception handling provides secure error responses

## Architecture

```
Controller Layer (REST API)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
Database (MySQL/H2)
```

## Integration Services

- **CRM Integration**: Sends reservation data to CRM system
- **Gastro Integration**: Sends reservation data to Gastro system
- Both integrations are asynchronous and don't block reservation creation

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.
