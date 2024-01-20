package com.example.brainAi.service;

import com.example.brainAi.dto.AuthenticationRequest;
import com.example.brainAi.dto.AuthenticationResponse;
import com.example.brainAi.entity.RefreshToken;
import com.example.brainAi.exceptions.AuthenticationServiceException;
import com.example.brainAi.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;
    private final RefreshTokenService refreshTokenService;

    /*
    private Connection getConnection() {
        // Implement your logic to get a database connection
        // For example, using a connection pool
        return YourConnectionProvider.getConnection();
    }
     */


    // The authenticate() method takes in an AuthenticationRequest object, which contains the username and password.
    // The method returns an AuthenticationResponse object, which contains the JWT and refresh token, and the user's roles.
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        // load the user details from the database using the username by calling the loadUserByUsername() method
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(authenticationRequest.getEmail());

        // check if the password matches the password in the database
        if (!passwordEncoder.matches(authenticationRequest.getPassword(), userDetails.getPassword())) {
            throw new AuthenticationServiceException("Invalid credentials");
        }

        // generate the JWT token
        String jwtToken = jwtUtil.generateToken(authenticationRequest, userDetails);

        // check if the token is already blacklisted
        if (tokenBlacklistService.isBlacklisted(jwtToken)) {
            throw new AuthenticationServiceException("Invalid credentials");
        }

        // create a refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
        // get the user's roles
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();

        // return the AuthenticationResponse object
        return new AuthenticationResponse(jwtToken, refreshToken.getToken(), roles);
    }

    /*
    public ResponseEntity<?> registerUser(String firstName, String lastName, String email, String password) {
        String query = "INSERT INTO users (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set parameters to the prepared statement
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, password);

            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                return ResponseEntity.status(HttpStatus.OK).body("User registered successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to register user");
            }

        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
     */
}
