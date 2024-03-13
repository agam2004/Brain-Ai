package com.example.brainAi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class corsConfig {
    private static final String[] ALLOWED_ORIGINS = {"http://localhost:3000", "http://localhost:8081", "http://localhost:5000"};

    // Bean for configuring CORS settings
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        // Return an anonymous implementation of WebMvcConfigurer

        return new WebMvcConfigurer() {

            // Override the addCorsMappings method to configure CORS settings
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Add mapping for the specific endpoint
                        .allowedOrigins(ALLOWED_ORIGINS)
                        .allowedMethods("*") // Only allow GET method for this endpoint
                        .allowCredentials(true);
            }
        };
    }
}
