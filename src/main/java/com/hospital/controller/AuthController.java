package com.hospital.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospital.entity.User;
import com.hospital.service.AuthService;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        try {

            if (user.getRole() == null ||
                    user.getRole().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Role is required");
            }

            String role = user.getRole()
                    .trim()
                    .toUpperCase();

            User savedUser;

            // =================================================
            // PATIENT REGISTRATION
            // =================================================

            if ("PATIENT".equals(role)) {

                savedUser = authService.registerPatient(user);

            }

            // =================================================
            // STAFF REGISTRATION
            // =================================================

            else {

                user.setRole(role);

                savedUser = authService.registerStaff(user);
            }

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedUser);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> loginRequest) {

        try {

            String email = loginRequest.get("email");
            String mobile = loginRequest.get("mobile");
            String password = loginRequest.get("password");

            String loginValue;

            if (email != null && !email.trim().isEmpty()) {

                loginValue = email.trim();

            } else if (mobile != null && !mobile.trim().isEmpty()) {

                loginValue = mobile.trim();

            } else {

                return ResponseEntity
                        .badRequest()
                        .body("Email or mobile is required");
            }

            if (password == null ||
                    password.trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Password is required");
            }

            String token =
                    authService.login(loginValue, password);

            /*
             * Find user again so that the frontend receives
             * userId, name and role along with the JWT.
             */
            User user;

            if (loginValue.contains("@")) {

                user = authService
                        .getAllUsers()
                        .stream()
                        .filter(u ->
                                loginValue.equalsIgnoreCase(
                                        u.getEmail()))
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"));

            } else {

                user = authService
                        .getAllUsers()
                        .stream()
                        .filter(u ->
                                loginValue.equals(
                                        u.getMobile()))
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"));
            }

            Map<String, Object> response =
                    new HashMap<>();

            response.put("message", "Login successful");
            response.put("token", token);
            response.put("userId", user.getId());
            response.put("name", user.getFullName());
            response.put("role", user.getRole());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }
    
//=========================================================
//UPDATE PATIENT PROFILE
//=========================================================


@PutMapping("/patient/profile")
public ResponseEntity<?> updatePatientProfile(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody Map<String, String> request) {

    try {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please log in first"));
        }

        User updatedUser = authService.updatePatientProfile(
                userDetails.getUsername(),
                request.get("fullName"),
                request.get("email"),
                request.get("oldPassword"),
                request.get("newPassword"),
                request.get("confirmNewPassword")
        );

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Profile updated successfully");
        response.put("userId", updatedUser.getId());
        response.put("fullName", updatedUser.getFullName());
        response.put("email", updatedUser.getEmail());
        response.put("mobile", updatedUser.getMobile());
        response.put("uhid", updatedUser.getUhid());

        return ResponseEntity.ok(response);

    } catch (RuntimeException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
    }
}



}