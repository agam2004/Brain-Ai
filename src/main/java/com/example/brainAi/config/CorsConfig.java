package com.example.brainAi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    // Bean for configuring CORS settings
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        // Return an anonymous implementation of WebMvcConfigurer
        return new WebMvcConfigurer() {

            // Override the addCorsMappings method to configure CORS settings
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Add a CORS mapping for all paths in the application
                registry.addMapping("/**")
                        // Allow requests from the specified origin (localhost:3000)
                        .allowedOrigins("http://localhost:3000")
                        // Allow specified HTTP methods for CORS requests
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH", "TRACE", "LOGOUT",
                                "CONNECT", "LINK", "UNLINK", "COPY", "LOCK", "UNLOCK", "VIEW", "LOGIN", "MERGE")
                        // Allow any headers in CORS requests
                        .allowedHeaders("*")
                        // Allow sending and receiving cookies in CORS requests
                        .allowCredentials(true);
            }
        };
    }
}