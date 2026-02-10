# 🔐 JWT Authentication & Authorization System

## 📋 Project Overview

A **production-ready JWT (JSON Web Token) authentication and authorization system** built with Spring Boot and Spring Security. This project demonstrates enterprise-grade security practices with stateless authentication, token refresh mechanisms, and role-based access control (RBAC).

**Key Highlight:** Implements industry-standard JWT security patterns for secure API authentication without session management.

---

## 🎯 Problem Statement & Solution

### Problem
Many modern applications require:
- Secure stateless authentication for distributed systems
- Token-based authorization for APIs
- Automatic token refresh without re-authentication
- Fine-grained access control based on user roles

### Solution
This project implements a complete JWT authentication system with:
- ✅ User registration and login with secure password encoding
- ✅ JWT token generation and validation
- ✅ Refresh token mechanism for seamless user experience
- ✅ Role-based access control (Admin, User, etc.)
- ✅ Comprehensive error handling and validation

---

## ✨ Key Features

### 🔑 Authentication & Authorization
- **User Registration** - Create new accounts with validation
- **User Login** - Generate JWT and refresh tokens securely
- **Token Refresh** - Extend session without re-login
- **Role-Based Access Control** - Protect endpoints by role
- **Password Encryption** - BCrypt-based password hashing

### 🛡️ Security Features
- JWT token-based authentication (stateless)
- Secure token validation with signature verification
- Automatic token expiration
- CORS configuration for cross-origin requests
- Input validation and error handling

### 📊 Product Management (Demo Feature)
- Retrieve products (accessible to authenticated users)
- Role-restricted endpoints demonstrating RBAC

---

## 🛠️ Technologies & Tools

### Backend Stack
| Technology | Purpose | Version |
|-----------|---------|---------|
| **Java** | Programming Language | 21 |
| **Spring Boot** | Web Framework | 4.0.2 |
| **Spring Security** | Authentication & Authorization | Latest |
| **Spring Data JPA** | Database Persistence | Latest |
| **MySQL** | Relational Database | 8.x |

### Libraries & Dependencies
- **JJWT** (io.jsonwebtoken) - JWT creation and verification
- **Lombok** - Boilerplate code reduction
- **Validation** - Bean validation and custom validators
- **Maven** - Build automation and dependency management

### Tools Used
- **Eclipse IDE / IntelliJ IDEA** - Development
- **Postman / Insomnia** - API testing
- **Maven** - Project build and dependencies
- **MySQL** - Database

---

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- MySQL 8.0+
- Git

### Installation & Setup

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/jwt-rebuild.git
   cd JWTRebuild
   ```

2. **Database Setup**
   ```sql
   CREATE DATABASE jwt_rebuild;
   ```

3. **Configure Database Connection**
   
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/jwt_rebuild
   spring.datasource.username=root
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

4. **Build the Project**
   ```bash
   mvn clean install
   ```

5. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

   The API will be available at: `http://localhost:8080`

---

## 📡 API Endpoints

### Authentication Endpoints

**POST /api/auth/signup** - User Registration
```json
Request:
{
  "email": "user@example.com",
  "password": "SecurePassword123!",
  "firstName": "John",
  "lastName": "Doe"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

**POST /api/auth/signin** - User Login
```json
Request:
{
  "email": "user@example.com",
  "password": "SecurePassword123!"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

**POST /api/auth/refresh** - Refresh Token
```json
Request:
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

### Protected Endpoints

**GET /api/products** - Get All Products (Requires Authentication)
```
Headers:
Authorization: Bearer <JWT_TOKEN>
```

---

## 🏗️ Project Architecture

### Directory Structure
```
JWTRebuild/
├── src/main/java/com/jwtrebuild/JWTRebuild/
│   ├── config/              # Security & application configuration
│   ├── controller/          # REST endpoints (AuthController, ProductController)
│   ├── service/             # Business logic (AuthService, JWTService)
│   ├── entity/              # Database entities (User, Product)
│   ├── dto/                 # Request/Response DTOs
│   ├── repository/          # JPA repositories
│   └── JwtRebuildApplication.java
└── src/main/resources/
    └── application.properties
```

### Key Components

**AuthService** - Handles user authentication logic
- User registration with input validation
- Password encoding and verification
- Token generation and validation

**JWTService** - JWT token operations
- Token creation with claims
- Token validation and expiration checks
- Custom claim extraction

**SecurityConfig** - Spring Security configuration
- JWT filter configuration
- Endpoint protection rules
- CORS settings

---

## 💡 Key Takeaways & Technical Highlights

### What Recruiters Should Notice

1. **Security Best Practices**
   - Stateless authentication using JWT
   - Secure password handling with BCrypt
   - Token expiration and refresh mechanisms
   - Request validation and error handling

2. **Clean Code Architecture**
   - Separation of concerns (Controller → Service → Repository)
   - Dependency injection using Spring
   - DTOs for API contracts
   - Proper exception handling

3. **Professional Practices**
   - Input validation with annotations
   - RESTful API design
   - Comprehensive error messages
   - Scalable and maintainable structure

4. **Real-World Applicable**
   - Production-ready authentication system
   - Suitable for microservices
   - Can be reused in other projects
   - Industry-standard implementation

---

## 🧪 Testing

Run unit tests:
```bash
mvn test
```

### Sample Test Scenarios
- User registration with valid/invalid inputs
- JWT token generation and validation
- Token refresh mechanism
- Role-based access control
- Authentication failure handling

---

## 📈 Performance & Scalability

- **Stateless Design** - No session storage required
- **Database Optimization** - Indexed user lookups
- **Token Caching** - Can be extended with Redis
- **Horizontal Scaling** - Works with multiple instances

---

## 🔒 Security Considerations

✅ **Implemented:**
- Secure password hashing (BCrypt)
- JWT signature verification
- CORS protection
- Input validation
- Exception handling

⚠️ **For Production:**
- Enable HTTPS/TLS
- Use environment variables for secrets
- Implement rate limiting
- Add request logging and monitoring
- Use secure token storage (HttpOnly cookies)

---

## 📚 What I Learned & Implemented

- Spring Security framework for authentication
- JWT token-based authorization patterns
- Database entity modeling and JPA
- RESTful API best practices
- Custom security filters and configuration
- Exception handling and validation strategies

---

## 🤝 Contributing

Contributions are welcome! Feel free to:
- Report bugs or issues
- Suggest improvements
- Submit pull requests

---

## 📄 License

This project is open source and available under the MIT License.

---

## 📞 Contact & Support

For questions, feedback, or opportunities:
- **Email:** rouennarin1235@gmail.com
- **LinkedIn:** [Narin Rouen](https://www.linkedin.com/in/narinrouen/)
- **GitHub:** [@narin-rouen](https://github.com/narin-rouen)

---

## 🎯 Career Relevance

This project demonstrates:
- ✅ Enterprise-level authentication system design
- ✅ Spring Boot and Spring Security expertise
- ✅ RESTful API development
- ✅ Database design and ORM usage
- ✅ Security best practices implementation
- ✅ Clean code and architectural patterns

**Perfect for:** Backend Developer, Full-Stack Developer, or Java Developer roles requiring security expertise.

---

**Created with ❤️ | Last Updated: February 2026**
