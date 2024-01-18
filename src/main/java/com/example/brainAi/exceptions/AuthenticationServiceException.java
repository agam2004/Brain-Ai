package com.example.brainAi.exceptions;

public class AuthenticationServiceException extends RuntimeException{
    public AuthenticationServiceException(String message) {
        super(message);
    }
}
