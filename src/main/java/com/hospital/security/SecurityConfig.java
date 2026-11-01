package com.hospital.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            // CORS
            .cors(cors ->
                    cors.configurationSource(
                            corsConfigurationSource()
                    )
            )

            // Disable CSRF because we are using JWT
            .csrf(csrf -> csrf.disable())

            // Stateless JWT authentication
            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            .authorizeHttpRequests(auth -> auth

                // AUTHENTICATION APIs
                .requestMatchers("/auth/**")
                .permitAll()

                // SWAGGER
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                )
                .permitAll()

                // CORS PREFLIGHT
                .requestMatchers(HttpMethod.OPTIONS, "/**")
                .permitAll()

                // ADMIN
                .requestMatchers("/admin/**")
                .hasRole("ADMIN")

                // DOCTOR SPECIALIZATION APIs
                .requestMatchers(
                        HttpMethod.GET,
                        "/doctor/specializations"
                )
                .hasAnyRole(
                        "PATIENT",
                        "DOCTOR",
                        "ADMIN",
                        "RECEPTIONIST"
                )

                .requestMatchers(
                        HttpMethod.GET,
                        "/doctor/specialization/**"
                )
                .hasAnyRole(
                        "PATIENT",
                        "DOCTOR",
                        "ADMIN",
                        "RECEPTIONIST"
                )

                // DOCTOR
                .requestMatchers("/doctor/**")
                .hasRole("DOCTOR")

                // NURSE
                .requestMatchers("/nurse/**")
                .hasRole("NURSE")

                // PATIENT
                .requestMatchers("/patient/**")
                .hasRole("PATIENT")

                // RECEPTIONIST
                .requestMatchers("/receptionist/**")
                .authenticated()

                // PHARMACIST
                .requestMatchers("/pharmacist/**")
                .authenticated()

                // LAB IN-CHARGE
                .requestMatchers("/lab/**")
                .authenticated()

                // BED IN-CHARGE
                .requestMatchers("/bed/**")
                .authenticated()

                // EVERYTHING ELSE
                .anyRequest()
                .authenticated()
            )

            // JWT FILTER
            .addFilterBefore(
                    jwtFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }


    // CORS CONFIGURATION
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        // Local development + deployed Vercel frontend
        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "https://hospital-managment-system-frontend-phi.vercel.app"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }


    // PASSWORD ENCODER
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // AUTHENTICATION MANAGER
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }
}