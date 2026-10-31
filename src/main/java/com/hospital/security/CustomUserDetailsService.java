package com.hospital.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hospital.entity.User;
import com.hospital.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: " + email
                        )
                );

        String role = user.getRole();

        if (role == null || role.trim().isEmpty()) {
            throw new UsernameNotFoundException(
                    "User role is missing for: " + email
            );
        }

        role = role.trim().toUpperCase();

        // Remove ROLE_ if it already exists
        if (role.startsWith("ROLE_")) {
            role = role.substring(5);
        }

        System.out.println("-----------------------------------------");
        System.out.println("USER LOADED");
        System.out.println("Email      : " + user.getEmail());
        System.out.println("User ID    : " + user.getId());
        System.out.println("Role in DB : " + user.getRole());
        System.out.println("Role used  : " + role);
        System.out.println("Status     : " + user.getStatus());
        System.out.println("-----------------------------------------");

        /*
         * .roles("RECEPTIONIST")
         * automatically creates:
         *
         * ROLE_RECEPTIONIST
         *
         * Same for:
         * ROLE_PHARMACIST
         * ROLE_LAB_INCHARGE
         * ROLE_BED_INCHARGE
         */
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(role)
                .build();
    }
}