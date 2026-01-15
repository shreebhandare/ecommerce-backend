# Ecommerce Backend API

A production-ready ecommerce backend built with Spring Boot, PostgreSQL, JWT authentication, and integrated payment processing.

## Features

### ✅ Authentication & Security
- JWT-based Authentication & Authorization
- User Registration & Login
- Role-based Access Control (USER/ADMIN)
- Password encryption with BCrypt
- Secure endpoint protection

### ✅ Product Management
- Complete CRUD operations
- Category-based organization
- Stock/Inventory tracking
- Product images and videos support
- Pagination and sorting
- Filter by category

### ✅ Shopping Cart
- Add/Update/Remove items
- Real-time stock validation
- Price snapshots (frozen at add-to-cart)
- Automatic cart clearing after order
- User-specific cart persistence

### ✅ Order Management
- Place orders from cart
- Order history with pagination
- Order status tracking (PENDING, PAID, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
- Cancel PENDING orders
- Price snapshots (frozen at order time)
- Complete order details with items

### ✅ Category Management
- Create/Read/Update/Delete categories
- Category images support
- Product count per category
- Prevent deletion of categories with products

### ✅ API Documentation
- Interactive Swagger UI
- OpenAPI 3.0 specification
- Built-in API testing interface
- JWT authentication support in docs

### ✅ Code Quality
- Input validation with Bean Validation
- Global exception handling
- RESTful API design
- DTO pattern implementation
- Transaction management

## Tech Stack

- **Backend:** Spring Boot 3.5.9
- **Database:** PostgreSQL 15
- **Security:** Spring Security + JWT (JJWT 0.12.3)
- **ORM:** Hibernate/JPA
- **Build Tool:** Maven
- **API Docs:** Springdoc OpenAPI 2.7.0
- **Containerization:** Docker Compose
- **Code Quality:** Lombok

## Prerequisites

- Java 17 or higher
- Docker & Docker Compose
- Maven 3.6+ (or use included wrapper)
- Postman or similar API testing tool (optional)

## Getting Started

### 1. Clone the Repository
```bash
git clone 
cd backend
```

### 2. Start PostgreSQL with Docker Compose
```bash
docker-compose up -d
```

### 3. Run the Application
```bash
./mvnw spring-boot:run
# or on Windows
.\mvnw.cmd spring-boot:run
```

The API will be available at `http://localhost:8080`

### 4. Access API Documentation
Open your browser and navigate to:
```
http://localhost:8080/swagger-ui/index.html
```

## API Endpoints

### Authentication
```http
POST   /api/auth/register    - Register new user
POST   /api/auth/login       - Login and get JWT token
```

### Categories
```http
POST   /api/categories       - Create category (Admin)
GET    /api/categories       - List all categories
GET    /api/categories/{id}  - Get category by ID
PUT    /api/categories/{id}  - Update category (Admin)
DELETE /api/categories/{id}  - Delete category (Admin)
```

### Products
```http
POST   /api/products              - Create product (Admin)
GET    /api/products              - List all products
GET    /api/products/paged        - List products (paginated)
GET    /api/products/{id}         - Get product by ID
DELETE /api/products/{id}         - Delete product (Admin)
```

**Pagination Query Parameters:**
- `page` (default: 0) - Page number
- `size` (default: 10) - Items per page
- `sortBy` (default: id) - Sort field (id, name, price)
- `sortDir` (default: asc) - Sort direction (asc, desc)

Example: `GET /api/products/paged?page=0&size=20&sortBy=price&sortDir=desc`

### Shopping Cart
```http
POST   /api/cart/items           - Add item to cart
GET    /api/cart                 - View cart
PUT    /api/cart/items/{id}      - Update item quantity
DELETE /api/cart/items/{id}      - Remove item from cart
DELETE /api/cart                 - Clear entire cart
```

### Orders
```http
POST   /api/orders               - Place order (checkout)
GET    /api/orders               - Order history
GET    /api/orders/paged         - Order history (paginated)
GET    /api/orders/{id}          - Get order details
POST   /api/orders/{id}/cancel   - Cancel pending order
```

## Example API Usage

### 1. Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "password": "SecurePass@123",
  "role": "USER"
}
```

### 2. Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "SecurePass@123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "johndoe",
  "role": "ROLE_USER"
}
```

### 3. Create Category
```http
POST /api/categories
Authorization: Bearer 
Content-Type: application/json

{
  "name": "Electronics",
  "description": "Electronic devices and gadgets",
  "imageUrl": "https://example.com/electronics.jpg"
}
```

### 4. Create Product
```http
POST /api/products
Authorization: Bearer 
Content-Type: application/json

{
  "name": "iPhone 15 Pro",
  "price": 999.99,
  "categoryId": 1,
  "stockQuantity": 50,
  "imageUrl": "https://example.com/iphone.jpg"
}
```

### 5. Add to Cart
```http
POST /api/cart/items
Authorization: Bearer 
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

### 6. Place Order
```http
POST /api/orders
Authorization: Bearer 
```

## Database Schema

### Users
- `id` - Primary key
- `username` - Unique username
- `password` - Encrypted password
- `role` - User role (ROLE_USER, ROLE_ADMIN)

### Categories
- `id` - Primary key
- `name` - Unique category name
- `description` - Category description
- `image_url` - Category thumbnail

### Products
- `id` - Primary key
- `name` - Product name
- `price` - Product price
- `category_id` - Foreign key to categories
- `stock_quantity` - Available inventory
- `image_url` - Product image
- `video_url` - Product video (optional)

### Carts
- `id` - Primary key
- `user_id` - Foreign key to users (unique)
- `created_at` - Creation timestamp
- `updated_at` - Last update timestamp

### Cart Items
- `id` - Primary key
- `cart_id` - Foreign key to carts
- `product_id` - Foreign key to products
- `quantity` - Item quantity
- `price_at_add` - Price snapshot when added

### Orders
- `id` - Primary key
- `user_id` - Foreign key to users
- `order_date` - Order timestamp
- `status` - Order status (PENDING, PAID, etc.)
- `total_amount` - Total order value

### Order Items
- `id` - Primary key
- `order_id` - Foreign key to orders
- `product_id` - Foreign key to products
- `quantity` - Item quantity
- `price_at_order` - Price snapshot at order time

## Configuration

Edit `src/main/resources/application.properties`:
```properties
# Application
spring.application.name=backend

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# JWT
jwt.secret=your_base64_encoded_secret_key
jwt.expiration=86400000
```

## Docker Commands
```bash
# Start PostgreSQL
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f postgres

# Stop and remove all data
docker-compose down -v

# Rebuild containers
docker-compose up -d --build
```

## Security Notes

### Password Requirements
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one number
- At least one special character (@$!%*?&)

### JWT Configuration
- Tokens expire after 24 hours (configurable)
- Secret key should be changed in production
- Use environment variables for sensitive data

## Project Structure
```
backend/
├── src/main/java/com/ecommerce/backend/
│   ├── config/          # Configuration classes
│   ├── controller/      # REST API endpoints
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # JPA entities
│   ├── exception/       # Custom exceptions & handlers
│   ├── repository/      # Database repositories
│   ├── security/        # Security configuration & JWT
│   └── service/         # Business logic
├── src/main/resources/
│   └── application.properties
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Development Roadmap

### ✅ Completed (Phase 1)
- [x] User authentication & authorization
- [x] Product CRUD with categories
- [x] Shopping cart functionality
- [x] Order management
- [x] Stock/Inventory tracking
- [x] Pagination & sorting
- [x] API documentation (Swagger)

### 🚧 In Progress (Phase 2)
- [ ] Payment integration (Stripe)
- [ ] Order status management
- [ ] Address management

### 📋 Planned (Phase 3)
- [ ] Product search & filtering
- [ ] Product reviews & ratings
- [ ] Email notifications
- [ ] File upload for images
- [ ] Admin dashboard endpoints
- [ ] Unit & integration tests
- [ ] Database migrations (Flyway)
- [ ] CI/CD pipeline
- [ ] Cloud deployment

## Testing

### Using Swagger UI
1. Navigate to `http://localhost:8080/swagger-ui/index.html`
2. Click "Authorize" button
3. Enter: `Bearer <your-jwt-token>`
4. Test any endpoint directly from browser

### Using Postman
1. Import the API endpoints
2. Set Authorization header: `Bearer <your-jwt-token>`
3. Test the complete user flow

## Contributing

This is an educational project. Feel free to fork and experiment!

## License

This project is for educational purposes.

## Author

**Shrikant Bhandare**

