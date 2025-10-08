package com.restaurant.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Profile("!prod")
@Configuration
public class TestProfileSecurityConfig {


    // No SecurityFilterChain here to avoid multiple anyRequest() chains in tests

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            authentication.setAuthenticated(true);
            return authentication;
        };
    }
}
