# springboot-microservices-backend
Production-style Spring Boot microservices backend with JWT authentication, API Gateway, MySQL, Docker, and AWS-ready deployment.

## Architecture
- API Gateway for request routing and authentication
- Auth Service for JWT-based authentication
- User Service for business operations
- MySQL as relational database
- Dockerized services for easy deployment

## Tech Stack
- Java 8+
- Spring Boot
- Spring Security & JWT
- Spring Data JPA
- MySQL
- Docker & Docker Compose
- AWS EC2 compatible
- Splunk for log monitoring

## Services
- auth-service: Handles authentication and JWT token generation
- user-service: Manages user-related business logic
- api-gateway: Routes requests and validates tokens
