package com.example.brainAi.service;

import com.example.brainAi.entity.RefreshToken;
import com.example.brainAi.entity.User;
import com.example.brainAi.repository.RefreshTokenRepository;
import com.example.brainAi.config.JwtProperties;
import com.example.brainAi.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    final private RefreshTokenRepository refreshTokenRepository;
    final private UserRepository userRepository;

    // createRefreshToken() method creates a new RefreshToken object and saves it to the database.
    @Transactional // This ensures that the method is wrapped in a transaction
    public RefreshToken createRefreshToken(String email) {

        User user = userRepository.findByEmail(email);

        // If a user doesn't exist, throw an exception or handle accordingly
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }


        // Check if the user refresh token exists in the database, delete it if it exists
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByUser(user);
        if (refreshTokenOptional.isPresent()) {

            // check if the refresh token is expired
            RefreshToken refreshToken = refreshTokenOptional.get();
            if (verifyExpiration(refreshToken) == null) {
                // delete the refresh token
                return null;
            }
            refreshTokenRepository.deleteByUser(userRepository.findByEmail(email));
            refreshTokenRepository.flush();
        }

        // Create a new one
        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(JwtProperties.EXPIRATION_TIME_FOR_REFRESH_TOKEN))
                .build();

        return refreshTokenRepository.save(newRefreshToken);
    }

    // find by token in the database, if it exists, then return the RefreshToken object.
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    // If the refresh token is valid, then it returns the RefreshToken object.
    public RefreshToken verifyExpiration(RefreshToken refreshToken) {

        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            refreshTokenRepository.flush();
            System.out.println("Refresh Token has expired. Please login again");
            return null;
        }
        return refreshToken;

    }

    @Transactional
    public void deleteRefreshTokenForUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            refreshTokenRepository.deleteByUser(user);
        }
    }
}
