
package com.hospital.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hospital.entity.User;
import com.hospital.repository.UserRepository;

@Service
public class PasswordResetService {

    private static final long OTP_EXPIRY_SECONDS = 300;
    private static final int MAX_ATTEMPTS = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResendEmailService resendEmailService;

    private final Map<String, OtpRecord> otpRecords =
            new ConcurrentHashMap<>();

    private final Map<String, ResetRecord> resetRecords =
            new ConcurrentHashMap<>();

    public PasswordResetService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ResendEmailService resendEmailService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.resendEmailService = resendEmailService;
    }

    public Map<String, Object> requestOtp(String rawEmail) {

        String email = normalizeEmail(rawEmail);

        Map<String, Object> response = new HashMap<>();

        response.put(
                "message",
                "If an account exists for this email address, an OTP has been sent."
        );

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return response;
        }

        String otp = String.format(
                "%06d",
                RANDOM.nextInt(1_000_000)
        );

        // Send OTP to the registered email.
        resendEmailService.sendOtpEmail(user.getEmail(), otp);

        otpRecords.put(
                email,
                new OtpRecord(
                        otp,
                        Instant.now().plusSeconds(OTP_EXPIRY_SECONDS)
                )
        );

        // Never return OTP to frontend.
        response.put("demoMode", false);

        return response;
    }

    public String verifyOtp(String rawEmail, String rawOtp) {

        String email = normalizeEmail(rawEmail);
        String otp = rawOtp == null ? "" : rawOtp.trim();

        OtpRecord record = otpRecords.get(email);

        if (record == null) {
            throw new IllegalArgumentException(
                    "OTP is invalid or expired. Request a new OTP."
            );
        }

        if (Instant.now().isAfter(record.expiresAt)) {
            otpRecords.remove(email);

            throw new IllegalArgumentException(
                    "OTP expired. Request a new OTP."
            );
        }

        if (record.attempts >= MAX_ATTEMPTS) {
            otpRecords.remove(email);

            throw new IllegalArgumentException(
                    "Too many incorrect attempts. Request a new OTP."
            );
        }

        if (!record.otp.equals(otp)) {
            record.attempts++;

            if (record.attempts >= MAX_ATTEMPTS) {
                otpRecords.remove(email);
            }

            throw new IllegalArgumentException(
                    "Incorrect OTP. Please try again."
            );
        }

        otpRecords.remove(email);

        String resetToken = UUID.randomUUID().toString();

        resetRecords.put(
                email,
                new ResetRecord(
                        resetToken,
                        Instant.now().plusSeconds(OTP_EXPIRY_SECONDS)
                )
        );

        return resetToken;
    }

    public void resetPassword(
            String rawEmail,
            String rawResetToken,
            String rawNewPassword) {

        String email = normalizeEmail(rawEmail);

        String resetToken =
                rawResetToken == null ? "" : rawResetToken.trim();

        String newPassword =
                rawNewPassword == null ? "" : rawNewPassword;

        if (newPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters."
            );
        }

        ResetRecord record = resetRecords.get(email);

        if (record == null
                || Instant.now().isAfter(record.expiresAt)
                || !record.token.equals(resetToken)) {

            resetRecords.remove(email);

            throw new IllegalArgumentException(
                    "Password reset session is invalid or expired. Verify OTP again."
            );
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unable to reset password for this account."
                ));

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        resetRecords.remove(email);
        otpRecords.remove(email);
    }

    private String normalizeEmail(String rawEmail) {

        String email = rawEmail == null
                ? ""
                : rawEmail.trim().toLowerCase();

        if (email.isBlank()
                || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

            throw new IllegalArgumentException(
                    "Enter a valid email address."
            );
        }

        return email;
    }

    private static class OtpRecord {

        private final String otp;
        private final Instant expiresAt;
        private int attempts;

        private OtpRecord(String otp, Instant expiresAt) {
            this.otp = otp;
            this.expiresAt = expiresAt;
        }
    }

    private static class ResetRecord {

        private final String token;
        private final Instant expiresAt;

        private ResetRecord(String token, Instant expiresAt) {
            this.token = token;
            this.expiresAt = expiresAt;
        }
    }
}
