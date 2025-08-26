package com.restaurant.booking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Restaurant Booking API")
                .description("A comprehensive REST API for managing restaurant table bookings, users, and reservations")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Restaurant Booking Team")
                    .email("support@restaurant-booking.com")
                    .url("https://restaurant-booking.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Local Development Server"),
                new Server()
                    .url("https://api.restaurant-booking.com")
                    .description("Production Server")
            ));
    }
}
