
package com.hospital.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospital.service.PasswordResetService;

@RestController
@RequestMapping("/auth")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(
            PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody Map<String, String> request) {
        try {
            Map<String, Object> response =
                    passwordResetService.requestOtp(request.get("email"));

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @RequestBody Map<String, String> request) {
        try {
            String resetToken = passwordResetService.verifyOtp(
                    request.get("email"),
                    request.get("otp")
            );

            Map<String, Object> response = new HashMap<>();
            response.put("message", "OTP verified successfully.");
            response.put("resetToken", resetToken);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody Map<String, String> request) {
        try {
            passwordResetService.resetPassword(
                    request.get("email"),
                    request.get("resetToken"),
                    request.get("newPassword")
            );

            Map<String, String> response = new HashMap<>();
            response.put(
                    "message",
                    "Password reset successfully. Please log in."
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<Map<String, String>> errorResponse(
            String message,
            HttpStatus status) {

        Map<String, String> response = new HashMap<>();
        response.put("message", message);

        return ResponseEntity.status(status).body(response);
    }
}
