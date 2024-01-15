package com.example.brainAi.config;

public class JwtProperties {
    public static final String SECRET = "SomeSecretForJWTGeneration";
    public static final int EXPIRATION_TIME = 10_000; // 10000 = 10 seconds
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    public static final int IDLE_TIME_FOR_REFRESH_TOKEN = 30_000; // 30 seconds, usually it should be 5 minutes

    public static final int EXPIRATION_TIME_FOR_REFRESH_TOKEN = EXPIRATION_TIME + IDLE_TIME_FOR_REFRESH_TOKEN;
}
