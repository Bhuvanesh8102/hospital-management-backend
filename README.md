# Hospital Management System

## Overview
The Hospital Management System is a web-based application designed to simplify hospital operations and improve patient management.

## Technologies Used

### Backend
- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- MySQL / TiDB Cloud
- Spring Security
- JWT Authentication

### Frontend
- React.js
- JavaScript
- HTML
- CSS

## Key Features
- User registration and login
- Role-based access
- Patient management
- Doctor management
- Appointment scheduling
- Consultation management
- Prescription management
- Laboratory test management
- Patient vitals management
- JWT-based authentication
- Email notifications
- REST API integration

## Database
The application uses a MySQL-compatible TiDB Cloud database.

## Backend Setup

1. Clone the repository.
2. Configure database credentials using environment variables.
3. Configure email credentials.
4. Run the application:

```bash
mvn spring-boot:run
```

Backend runs on:

`http://localhost:8080`

## API Documentation

Swagger UI:

`http://localhost:8080/swagger-ui.html`

## Project Purpose
To provide a centralized platform for managing hospital activities, patient information, appointments, consultations, prescriptions, and laboratory records.