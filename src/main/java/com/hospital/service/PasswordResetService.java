
package com.hospital.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.password-reset.demo-mode:false}")
    private boolean demoMode;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ResendEmailService resendEmailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.resendEmailService = resendEmailService;
    }

    public Map<String, Object> requestOtp(String rawMobile) {
        String mobile = normalizeMobile(rawMobile);

        Map<String, Object> response = new HashMap<>();
        response.put(
                "message",
                "If an account exists for this mobile number, an OTP has been sent to its registered email."
        );

        User user = userRepository.findByMobile(mobile).orElse(null);

        if (user == null) {
            return response;
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return response;
        }

        String otp = String.format(
                "%06d",
                RANDOM.nextInt(1_000_000)
        );

        // Send the OTP before saving it as an active OTP.
        resendEmailService.sendOtpEmail(user.getEmail(), otp);

        otpRecords.put(
                mobile,
                new OtpRecord(
                        otp,
                        Instant.now().plusSeconds(OTP_EXPIRY_SECONDS)
                )
        );

        // Never return the OTP to the frontend.
        response.put("demoMode", false);

        return response;
    }

    public String verifyOtp(String rawMobile, String rawOtp) {
        String mobile = normalizeMobile(rawMobile);
        String otp = rawOtp == null ? "" : rawOtp.trim();

        OtpRecord record = otpRecords.get(mobile);

        if (record == null) {
            throw new IllegalArgumentException(
                    "OTP is invalid or expired. Request a new OTP."
            );
        }

        if (Instant.now().isAfter(record.expiresAt)) {
            otpRecords.remove(mobile);
            throw new IllegalArgumentException(
                    "OTP expired. Request a new OTP."
            );
        }

        if (record.attempts >= MAX_ATTEMPTS) {
            otpRecords.remove(mobile);
            throw new IllegalArgumentException(
                    "Too many incorrect attempts. Request a new OTP."
            );
        }

        if (!record.otp.equals(otp)) {
            record.attempts++;

            if (record.attempts >= MAX_ATTEMPTS) {
                otpRecords.remove(mobile);
            }

            throw new IllegalArgumentException(
                    "Incorrect OTP. Please try again."
            );
        }

        otpRecords.remove(mobile);

        String resetToken = UUID.randomUUID().toString();

        resetRecords.put(
                mobile,
                new ResetRecord(
                        resetToken,
                        Instant.now().plusSeconds(OTP_EXPIRY_SECONDS)
                )
        );

        return resetToken;
    }

    public void resetPassword(
            String rawMobile,
            String rawResetToken,
            String rawNewPassword) {

        String mobile = normalizeMobile(rawMobile);

        String resetToken =
                rawResetToken == null ? "" : rawResetToken.trim();

        String newPassword =
                rawNewPassword == null ? "" : rawNewPassword;

        if (newPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters."
            );
        }

        ResetRecord record = resetRecords.get(mobile);

        if (record == null
                || Instant.now().isAfter(record.expiresAt)
                || !record.token.equals(resetToken)) {

            resetRecords.remove(mobile);

            throw new IllegalArgumentException(
                    "Password reset session is invalid or expired. Verify OTP again."
            );
        }

        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unable to reset password for this account."
                ));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetRecords.remove(mobile);
        otpRecords.remove(mobile);
    }

    private String normalizeMobile(String rawMobile) {
        String mobile = rawMobile == null ? "" : rawMobile.trim();

        if (!mobile.matches("[0-9]{10}")) {
            throw new IllegalArgumentException(
                    "Enter a valid 10-digit mobile number."
            );
        }

        return mobile;
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
