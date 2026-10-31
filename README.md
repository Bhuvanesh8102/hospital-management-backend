# Hospital Management System Backend

Spring Boot monolithic backend for the Hospital Management System.

## Structure

- entity
- repository
- service
- service/serviceimpl
- controller
- security
- config

## Roles

ADMIN, DOCTOR, NURSE, RECEPTIONIST, PATIENT, PHARMACIST, LAB_INCHARGE, BED_INCHARGE

## Database

MySQL database:
hospital_management_db

Default application configuration:
- Host: localhost
- Port: 3306
- Username: root
- Password: empty

If your MySQL password is different, update:
src/main/resources/application.properties

## Run

1. Open as a Maven project in Eclipse/STS/IntelliJ.
2. Make sure MySQL is running.
3. Update database username/password if required.
4. Run HospitalManagementSystemApplication.java.

Swagger:
http://localhost:8080/swagger-ui.html

Register:
POST /auth/register

Login:
POST /auth/login

After login, use the returned JWT in Swagger Authorize:
Bearer <token>

Note:
This ZIP provides the complete project structure and the core authentication/user workflow. Additional role-specific business methods can be expanded as the frontend/API requirements are implemented.
