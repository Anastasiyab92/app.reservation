package com.restaurant.booking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class SimpleSwaggerConfig {
    // This configuration will only be active in production
    // Swagger will be disabled via application-prod.properties
}
