# Charity Platform

A full-stack web application for managing charity activities, donations, and aid requests. Built with Spring Boot (backend), React (frontend), and SQL Server (database).

## Features
- User authentication (login, registration)
- Role-based access control (Admin, Donor, Organization, Beneficiary)
- Create and manage charity projects
- Submit and track aid requests
- Online donations with VNPay integration
- Real-time updates with WebSockets
- Search, filter, and pagination for requests and projects

## Tech Stack
### Backend (Spring Boot)
- Spring Boot 3.x
- Spring Security (JWT Authentication)
- Spring Data JPA (Hibernate)
- SQL Server (Database)
- Lombok
- MapStruct (DTO Mapping)
- WebSockets (Real-time updates)

### Frontend (React)
- React 18+
- Vite (for fast development)
- Tailwind CSS
- Axios (API calls)
- React Router
- Redux Toolkit (State management)

### Database
- SQL Server

## Setup Instructions

### Prerequisites
- Java 17+
- Node.js 18+
- SQL Server
- Maven

### Backend Setup
1. Clone the repository:
   ```sh
   git clone repository_url
   ```
2. Configure the database in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=charity_db;encrypt=false
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```
3. Run the backend:
   ```sh
   mvn spring-boot:run
   ```
   
### API Documentation
Once the backend is running, using Postman to test api at:
```
http://localhost:8080/[controller]/[name_of_request]
```

## Contributing
Feel free to submit issues or create pull requests to improve the project.

## License
MIT License

