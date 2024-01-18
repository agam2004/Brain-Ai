package com.example.brainAi.dto;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
    private String jwtToken;
}
