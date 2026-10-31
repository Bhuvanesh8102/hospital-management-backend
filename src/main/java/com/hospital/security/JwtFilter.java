package com.hospital.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        System.out.println();
        System.out.println("========================================");
        System.out.println("JWT FILTER");
        System.out.println("Request: " + requestURI);

        String authorizationHeader =
                request.getHeader("Authorization");

        // No JWT token
        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            System.out.println("Authorization header: NOT FOUND");

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        System.out.println("Authorization header: FOUND");
        System.out.println("Token received");

        try {

            // Get email from JWT
            String email = jwtService.extractEmail(token);

            System.out.println("Email from JWT: " + email);

            if (email != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                // Load user from database
                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(email);

                System.out.println(
                        "User authorities: "
                        + userDetails.getAuthorities()
                );

                // Validate JWT
                boolean valid =
                        jwtService.isTokenValid(
                                token,
                                email
                        );

                System.out.println(
                        "JWT valid: " + valid
                );

                if (valid) {

                    UsernamePasswordAuthenticationToken
                            authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);

                    System.out.println(
                            "AUTHENTICATION SUCCESS"
                    );

                    System.out.println(
                            "Logged-in user: "
                            + userDetails.getUsername()
                    );

                    System.out.println(
                            "Authorities: "
                            + userDetails.getAuthorities()
                    );

                    // Final authentication check
                    System.out.println(
                            "FINAL AUTH USER: "
                            + SecurityContextHolder
                                    .getContext()
                                    .getAuthentication()
                                    .getName()
                    );

                    System.out.println(
                            "FINAL AUTHORITIES: "
                            + SecurityContextHolder
                                    .getContext()
                                    .getAuthentication()
                                    .getAuthorities()
                    );

                    System.out.println(
                            "REQUEST URI: "
                            + requestURI
                    );

                } else {

                    System.out.println(
                            "AUTHENTICATION FAILED: INVALID TOKEN"
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "JWT ERROR: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }

        System.out.println("========================================");

        filterChain.doFilter(request, response);
    }
}