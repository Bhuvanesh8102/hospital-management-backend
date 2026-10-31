package com.hospital.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hospital.entity.User;
import com.hospital.repository.UserRepository;
import com.hospital.security.JwtService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // =========================================================
    // PATIENT REGISTRATION
    // =========================================================

    @Override
    public User registerPatient(User user) {

        // Check email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Check mobile
        if (userRepository.findByMobile(user.getMobile()).isPresent()) {
            throw new RuntimeException("Mobile number already registered");
        }

        // Set role
        user.setRole("PATIENT");

        // Generate Patient ID
        user.setId(generateUserId("PATIENT"));

        // Generate UHID
        user.setUhid(generateUhid());

        // Encrypt password
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        // Patients are immediately active
        user.setStatus("ACTIVE");

        return userRepository.save(user);
    }


    // =========================================================
    // STAFF REGISTRATION
    // =========================================================

    @Override
    public User registerStaff(User user) {

        // Check email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Check mobile
        if (userRepository.findByMobile(user.getMobile()).isPresent()) {
            throw new RuntimeException("Mobile number already registered");
        }

        // Check role
        String role = user.getRole();

        if (role == null || role.trim().isEmpty()) {
            throw new RuntimeException("Staff role is required");
        }

        role = role.trim().toUpperCase();

        // Only staff roles are allowed here
        if (!isValidStaffRole(role)) {
            throw new RuntimeException("Invalid staff role");
        }

        user.setRole(role);

        // Generate staff ID
        user.setId(generateUserId(role));

        // Encrypt password
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        // Staff requires admin approval
        user.setStatus("PENDING");

        return userRepository.save(user);
    }


    // =========================================================
    // LOGIN
    // =========================================================

    @Override
    public String login(String email, String password) {

        User user;

        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email or mobile is required");
        }

        email = email.trim();

        // Login using email
        if (email.contains("@")) {

            user = userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Invalid email or password"
                            )
                    );

        } else {

            // Login using mobile
            user = userRepository.findByMobile(email)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Invalid mobile or password"
                            )
                    );
        }

        // Check password
        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid email/mobile or password"
            );
        }

        // =====================================================
        // CHECK USER STATUS
        // =====================================================

        String status = user.getStatus();

        if (status == null) {
            throw new RuntimeException(
                    "User status is not available"
            );
        }

        // Staff waiting for admin approval
        if ("PENDING".equalsIgnoreCase(status)) {

            throw new RuntimeException(
                    "Your account is waiting for admin approval"
            );
        }

        // Staff rejected
        if ("REJECTED".equalsIgnoreCase(status)) {

            throw new RuntimeException(
                    "Your staff registration has been rejected"
            );
        }

        // Deactivated staff
        if ("INACTIVE".equalsIgnoreCase(status)) {

            throw new RuntimeException(
                    "Your account is inactive"
            );
        }

        // =====================================================
        // GENERATE JWT
        // =====================================================

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole()
        );

        return token;
    }


    // =========================================================
    // GET ALL USERS
    // =========================================================

    @Override
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }


    // =========================================================
    // GENERATE USER ID
    // =========================================================

    private Long generateUserId(String role) {

        long startId;

        switch (role.toUpperCase()) {

            case "PATIENT":
                startId = 10001;
                break;

            case "ADMIN":
                startId = 20001;
                break;

            case "DOCTOR":
                startId = 30001;
                break;

            case "NURSE":
                startId = 40001;
                break;

            case "RECEPTIONIST":
                startId = 50001;
                break;

            case "PHARMACIST":
                startId = 60001;
                break;

            case "LAB_INCHARGE":
                startId = 70001;
                break;

            case "BED_INCHARGE":
                startId = 80001;
                break;

            default:
                throw new RuntimeException(
                        "Invalid staff role"
                );
        }

        Long maxId = userRepository
                .findByRole(role)
                .stream()
                .map(User::getId)
                .max(Long::compareTo)
                .orElse(startId - 1);

        return maxId + 1;
    }


    // =========================================================
    // VALIDATE STAFF ROLE
    // =========================================================

    private boolean isValidStaffRole(String role) {

        return role.equals("DOCTOR")
                || role.equals("NURSE")
                || role.equals("RECEPTIONIST")
                || role.equals("PHARMACIST")
                || role.equals("LAB_INCHARGE")
                || role.equals("BED_INCHARGE");
    }


    // =========================================================
    // GENERATE UHID
    // =========================================================

    private String generateUhid() {

        long number = 10001;

        List<User> patients =
                userRepository.findByRole("PATIENT");

        if (!patients.isEmpty()) {

            number = patients.stream()
                    .map(User::getUhid)
                    .filter(uhid -> uhid != null)
                    .filter(uhid -> uhid.startsWith("UHID"))
                    .map(uhid -> {

                        try {
                            return Long.parseLong(
                                    uhid.substring(4)
                            );
                        } catch (Exception e) {
                            return 10000L;
                        }

                    })
                    .max(Long::compareTo)
                    .orElse(10000L) + 1;
        }

        return "UHID" + number;
    }
    
    @Override
    public User registerWalkInPatient(User user) {

        // Check email if provided
        if (user.getEmail() != null
                && !user.getEmail().trim().isEmpty()) {

            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                throw new RuntimeException("Email already registered");
            }
        }

        // Mobile is required
        if (user.getMobile() == null
                || user.getMobile().trim().isEmpty()) {

            throw new RuntimeException("Mobile number is required");
        }

        // Check mobile
        if (userRepository.findByMobile(user.getMobile()).isPresent()) {
            throw new RuntimeException(
                    "Mobile number already registered");
        }

        // Set patient role
        user.setRole("PATIENT");

        // Generate Patient ID
        user.setId(generateUserId("PATIENT"));

        // Generate UHID
        user.setUhid(generateUhid());

        // Use mobile number as initial password
        user.setPassword(
                passwordEncoder.encode(user.getMobile())
        );

        // Walk-in patient is active immediately
        user.setStatus("ACTIVE");

        return userRepository.save(user);
    }

@Override
public User updatePatientProfile(
        String loggedInEmail,
        String fullName,
        String email,
        String oldPassword,
        String newPassword,
        String confirmNewPassword) {

    User patient = userRepository.findByEmail(loggedInEmail)
            .orElseThrow(() ->
                    new RuntimeException("Patient not found"));

    if (!"PATIENT".equalsIgnoreCase(patient.getRole())) {
        throw new RuntimeException(
                "Only patients can update this profile");
    }

    if (fullName == null || fullName.trim().isEmpty()) {
        throw new RuntimeException("Full name is required");
    }

    if (email == null || email.trim().isEmpty()
            || !email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
        throw new RuntimeException("Enter a valid email address");
    }

    String updatedEmail = email.trim();

    // Check whether another user already has this email
    userRepository.findByEmail(updatedEmail)
            .ifPresent(existingUser -> {
                if (!existingUser.getId().equals(patient.getId())) {
                    throw new RuntimeException(
                            "Email is already registered");
                }
            });

    boolean changingPassword =
            (oldPassword != null && !oldPassword.isEmpty())
            || (newPassword != null && !newPassword.isEmpty())
            || (confirmNewPassword != null
                    && !confirmNewPassword.isEmpty());

    // Validate the password before updating profile fields
    if (changingPassword) {

        if (oldPassword == null || oldPassword.isEmpty()
                || !passwordEncoder.matches(
                        oldPassword, patient.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException(
                    "New password must contain at least 8 characters");
        }

        if (newPassword.equals(oldPassword)) {
            throw new RuntimeException(
                    "New password must differ from the old password");
        }

        if (confirmNewPassword == null
                || !newPassword.equals(confirmNewPassword)) {
            throw new RuntimeException(
                    "New password and confirmation do not match");
        }

        patient.setPassword(passwordEncoder.encode(newPassword));
    }

    // Update only the permitted profile fields
    patient.setFullName(fullName.trim());
    patient.setEmail(updatedEmail);

    // Mobile, UHID, role, and status remain unchanged
    return userRepository.save(patient);
}

}
