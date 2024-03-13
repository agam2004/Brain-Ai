package com.example.brainAi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@RestController
public class modelController {

    private final String FLASK_SERVER_URL = "http://localhost:5000"; // Update with your Flask server URL

    @GetMapping("/generate_images")
    public ResponseEntity<?> generateImage() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String flaskEndpoint = FLASK_SERVER_URL + "/generate_images";

            // Make a GET request to the Flask server's /generate_image endpoint
            return ResponseEntity.ok(restTemplate.getForEntity(flaskEndpoint, String.class));
        } catch (HttpClientErrorException e) {
            // Handle HttpClientErrorException (e.g., 404, 500, etc.)
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }
}
