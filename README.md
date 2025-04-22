# Spring Boot Bidding Application API

A comprehensive RESTful API for an online bidding and auction platform built with Spring Boot. This application allows users to create auctions, place bids, communicate through real-time chat, and process payments.

## Features

- **User Authentication and Authorization**
  - JWT-based authentication
  - Role-based access control
  - Password reset functionality

- **Auction Management**
  - Create, update, and delete auctions
  - Set auction parameters (starting price, duration, etc.)
  - View auction details and status

- **Bidding System**
  - Place bids on active auctions
  - View bid history
  - Automatic winner determination

- **Real-time Chat**
  - Communication between buyers and sellers
  - Message history
  - Real-time notifications

- **Payment Processing**
  - Integration with PayStack payment gateway
  - Secure payment handling

- **Subscription Management**
  - Different subscription plans
  - Subscription status tracking

## Technologies Used

- **Backend**
  - Java 17
  - Spring Boot
  - Spring Security with JWT
  - Spring Data JPA
  - Spring WebSocket for real-time communication
  - Hibernate ORM

- **Database**
  - PostgreSQL

- **DevOps**
  - Docker
  - Docker Compose

- **Payment Integration**
  - PayStack API

## Prerequisites

- Java 17 or higher
- Maven 3.8.5 or higher
- PostgreSQL database
- Docker and Docker Compose (for containerized deployment)

## Setup and Installation

### Environment Variables

Create a `.env` file in the root directory with the following variables:

```
ACTIVE_PROFILE=dev
POSTGRESQL_HOST=localhost
POSTGRESQL_PORT=5432
POSTGRESQL_DATABASE=bid_db
POSTGRESQL_USERNAME=your_username
POSTGRESQL_PASSWORD=your_password
EMAIL_HOST=smtp.example.com
EMAIL_PORT=587
EMAIL_USERNAME=your_email@example.com
EMAIL_PASSWORD=your_email_password
FRONTEND_URL=http://localhost:3000
SECRET_KEY=your_jwt_secret_key
EXPIRATION=86400000
PAYSTACK_SECRET_KEY=your_paystack_secret_key
```

### Running Locally

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/springboot-bidding-app-api.git
   cd springboot-bidding-app-api
   ```

2. Build the application:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. The API will be available at `http://localhost:8080/api/v1/`

### Running with Docker

1. Build and start the containers:
   ```bash
   docker-compose up -d
   ```

2. Build the Docker image:
   ```bash
   docker build -t bidding-app-api .
   ```

3. Run the Docker container:
   ```bash
   docker run -p 8080:8080 --env-file .env bidding-app-api
   ```

## API Endpoints

### Authentication

- `POST /api/v1/auth/register` - Register a new user
- `POST /api/v1/auth/login` - Authenticate a user
- `POST /api/v1/auth/forgot-password` - Request password reset
- `POST /api/v1/auth/reset-password` - Reset password with token

### Auctions

- `GET /api/v1/auctions` - Get all auctions
- `GET /api/v1/auctions/{id}` - Get auction by ID
- `POST /api/v1/auctions` - Create a new auction
- `PUT /api/v1/auctions/{id}` - Update an auction
- `DELETE /api/v1/auctions/{id}` - Delete an auction

### Bids

- `GET /api/v1/bids` - Get all bids
- `GET /api/v1/bids/{id}` - Get bid by ID
- `POST /api/v1/bids` - Place a new bid
- `GET /api/v1/auctions/{id}/bids` - Get all bids for an auction

### Chat and Messages

- `GET /api/v1/chats` - Get all chats
- `GET /api/v1/chats/{id}` - Get chat by ID
- `POST /api/v1/chats` - Create a new chat
- `GET /api/v1/messages` - Get all messages
- `POST /api/v1/messages` - Send a new message

### Payments

- `POST /api/v1/payments/initialize` - Initialize a payment
- `GET /api/v1/payments/verify/{reference}` - Verify a payment

### Subscriptions

- `GET /api/v1/subscriptions` - Get all subscriptions
- `GET /api/v1/subscriptions/{id}` - Get subscription by ID
- `POST /api/v1/subscriptions` - Create a new subscription
- `PUT /api/v1/subscriptions/{id}` - Update a subscription

## Security

The API uses JWT (JSON Web Tokens) for authentication. To access protected endpoints, include the JWT token in the Authorization header:

```
Authorization: Bearer your_jwt_token
```

## Error Handling

The API returns appropriate HTTP status codes and error messages in case of failures:

- `200 OK` - Request succeeded
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid request parameters
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server-side error

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
