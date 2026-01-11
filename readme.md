# Ecommerce Backend API

A production-ready ecommerce backend built with Spring Boot, PostgreSQL, and JWT authentication.

## Features

- ✅ JWT Authentication & Authorization
- ✅ User Registration & Login
- ✅ Product Management (CRUD)
- ✅ Shopping Cart (Coming Soon)
- ✅ Order Management (Coming Soon)
- ✅ Role-based Access Control
- ✅ Input Validation
- ✅ Global Exception Handling
- ✅ Docker Compose Setup

## Tech Stack

- **Backend:** Spring Boot 3.x
- **Database:** PostgreSQL 15
- **Security:** Spring Security + JWT
- **ORM:** Hibernate/JPA
- **Build Tool:** Maven
- **Containerization:** Docker Compose
- **Code Quality:** Lombok

## Prerequisites

- Java 17 or higher
- Docker & Docker Compose
- Maven 3.6+ (or use included wrapper)

## Getting Started

### 1. Clone the Repository
```bash
git clone <your-repo-url>
cd backend
```

### 2. Start PostgreSQL with Docker Compose
```bash
docker-compose up -d
```

### 3. Run the Application
```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

## API Endpoints

### Authentication

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "Test@123",
  "role": "USER"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "Test@123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "testuser",
  "role": "ROLE_USER"
}
```

### Products (Protected - Requires Authentication)

#### Get All Products
```http
GET /api/products
Authorization: Bearer <your-jwt-token>
```

#### Get Product by ID
```http
GET /api/products/{id}
Authorization: Bearer <your-jwt-token>
```

#### Create Product
```http
POST /api/products
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "name": "Laptop",
  "price": 999.99
}
```

#### Delete Product
```http
DELETE /api/products/{id}
Authorization: Bearer <your-jwt-token>
```

## Database Schema

### Users Table
- `id` (BIGINT, Primary Key)
- `username` (VARCHAR, Unique)
- `password` (VARCHAR, Encrypted)
- `role` (VARCHAR)

### Products Table
- `id` (BIGINT, Primary Key)
- `name` (VARCHAR)
- `description` (VARCHAR)
- `price` (DOUBLE)
- `stock_quantity` (INTEGER)
- `category` (VARCHAR)
- `image_url` (VARCHAR)
- `video_url` (VARCHAR)

## Configuration

Edit `src/main/resources/application.properties`:
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
spring.datasource.username=postgres
spring.datasource.password=Password

# JWT
jwt.secret=<your-secret-key>
jwt.expiration=86400000
```

## Docker Commands
```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f

# Stop and remove volumes (deletes data)
docker-compose down -v
```

## Password Requirements

- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one number
- At least one special character (@$!%*?&)

## Project Structure
```
backend/
├── src/main/java/com/ecommerce/backend/
│   ├── controller/      # REST API endpoints
│   ├── service/         # Business logic
│   ├── repository/      # Database access
│   ├── entity/          # Database entities
│   ├── dto/             # Data Transfer Objects
│   ├── security/        # JWT & security config
│   └── exception/       # Error handling
├── src/main/resources/
│   └── application.properties
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Future Enhancements

- [ ] Shopping Cart functionality
- [ ] Order management
- [ ] Payment integration
- [ ] Product search & filtering
- [ ] Pagination
- [ ] File upload for product images
- [ ] Email notifications
- [ ] Admin dashboard

## License

This project is for educational purposes.

## Author

Shrikant Bhandare