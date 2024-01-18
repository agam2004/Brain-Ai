package com.example.brainAi.service;

import com.example.brainAi.dto.AuthenticationResponse;
import com.example.brainAi.util.JwtProperties;
import com.example.brainAi.entity.RefreshToken;
import com.example.brainAi.entity.User;
import com.example.brainAi.exceptions.TokenRefreshException;
import com.example.brainAi.repository.RefreshTokenRepository;
import com.example.brainAi.repository.UserRepository;
import com.example.brainAi.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    final private RefreshTokenRepository refreshTokenRepository;
    final private UserRepository userRepository;
    final private CustomUserDetailsService customUserDetailsService;
    final private JwtUtil jwtUtil;

    // createRefreshToken() method creates a new RefreshToken object and saves it to the database.
    // It must be wrapped in a transaction, so that the database is updated, this is done in the refresh() method,
    // but it is also called directly from the AuthenticationService class, so it is wrapped in a transaction here as well.
    @Transactional // This ensures that the method is wrapped in a transaction
    public RefreshToken createRefreshToken(String email) {

        // Find the user by email
        User user = userRepository.findByEmail(email);
        // If a user doesn't exist, throw an exception or handle accordingly
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        // first, check if the user refresh token exists in the database, delete it if it exists
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByUser(user);
        if (refreshTokenOptional.isPresent()) {
            deleteRefreshTokenForUser(email);
        }

        // create a new refresh token, for the user
        try {
            RefreshToken newRefreshToken = RefreshToken.builder()
                    .user(user)
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusMillis(JwtProperties.EXPIRATION_TIME))
                    .build();

            return refreshTokenRepository.save(newRefreshToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating refresh token");
        }
    }

    // delete the refresh token for the user
    // it must be wrapped in a transaction, so that the database is updated, this is done in the refresh() method,
    // see below , which calls creteRefreshToken() method in turn, see above
    public void deleteRefreshTokenForUser(String email) {
        User user = userRepository.findByEmail(email);
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();
    }

    // find by token in the database, if it exists, then return the RefreshToken object.
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    // check If the refresh token is valid
    public boolean verifyExpiration(RefreshToken refreshToken) {

        // check if the refresh token has expired
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            refreshTokenRepository.flush();
            System.out.println("Refresh Token has expired. Please login again");
            return false;
        }

        return true;
    }

    @Transactional
    // this should be called when the user requests a new JWT token using the refresh token
    // it must be wrapped in a transaction, so that the database is updated
    public AuthenticationResponse refresh(String refreshToken) {
        // check if the refresh token exists in the database
        Optional<RefreshToken> refreshTokenEntity = findByToken(refreshToken);
        // if it doesn't exist, throw an exception or handle accordingly
        refreshTokenEntity.orElseThrow(() -> new TokenRefreshException("Refresh token is invalid"));

        // if it exists, check if it is valid
        RefreshToken validRefreshToken = refreshTokenEntity.get();
        // if it is not valid, throw an exception or handle accordingly
        if (!verifyExpiration(validRefreshToken)) {
            System.out.println("Refresh Token has expired. Please login again");
            throw new TokenRefreshException("Refresh token has expired. Please log in again");
        }

        // if it is valid, find the user by email
        String userEmail = validRefreshToken.getUser().getEmail();
        // userDetailsService.loadUserByUsername() method is called to load the user details
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
        // generate a new JWT token from the username
        String newJwtToken = jwtUtil.generateTokenFromUsername(userDetails.getUsername());
        // create a new refresh token for the user
        RefreshToken newRefreshToken = createRefreshToken(userEmail);
        // return the new JWT token and the new refresh token
        return new AuthenticationResponse(newJwtToken, newRefreshToken.getToken(), userDetails.getAuthorities());
    }
}
