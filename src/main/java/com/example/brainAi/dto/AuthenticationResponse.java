package com.example.brainAi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticationResponse {
    // Instance variable to store the access token (JWT token) of the user
    private String accessToken;
    // Instance variable to store the roles assigned to the user
    private List<String> roles;
    private String refreshToken;

    // Constructor to initialize the accessToken and roles from the given parameters
    public AuthenticationResponse(String accessToken, String refreshToken, Collection<? extends GrantedAuthority> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        // Convert the roles a collection to a list of role names (authorities) using Java Streams
        this.roles = roles.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }
    // Constructor to initialize the accessToken and roles from the given parameters
    public AuthenticationResponse(String accessToken, Collection<? extends GrantedAuthority> roles) {
        this.accessToken = accessToken;
        // Convert the roles a collection to a list of role names (authorities) using Java Streams
        this.roles = roles.stream().map(GrantedAuthority::getAuthority).toList();
    }

    // Getter method to retrieve the JWT token, which is the same as the access token
    public String getJwtToken() {
        return this.accessToken;
    }
}
