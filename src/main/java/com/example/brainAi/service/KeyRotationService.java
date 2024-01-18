package com.example.brainAi.service;

import com.example.brainAi.entity.RSAKeysEntity;
import com.example.brainAi.repository.RSAKeysRepository;
import com.example.brainAi.util.JwtUtil;
import com.example.brainAi.util.RSAKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KeyRotationService {
    private final JwtUtil jwtUtil;
    private final RSAKeysRepository RSAKeysRepository; // Assume this is your way to store private keys
    private final RSAKeyGenerator RSAKeyGenerator;

    @Transactional
    public void rotateKeys() {
        try {
            // Generate a new key pair
            KeyPair newKeyPair = RSAKeyGenerator.generateKeyPair();

            // log the new keys, for debugging purposes
            System.out.println("New private key: " + newKeyPair.getPrivate());
            System.out.println("New public key: " + newKeyPair.getPublic());

            // Check if the database already has keys. If yes, rotate the keys.
            Optional<RSAKeysEntity> currentKeysOptional = RSAKeysRepository.findById(1);
            if (currentKeysOptional.isPresent()) {
                RSAKeysEntity currentRSAKeysEntity = currentKeysOptional.get();

                // Debug: Print current keys before rotation
                System.out.println("Current keys before rotation - Private: " +
                        Arrays.toString(currentRSAKeysEntity.getPrivate_key()) + ", Public: " +
                        Arrays.toString(currentRSAKeysEntity.getPublic_key()));

                // Create as a new key (kid 2) with the old key data
                RSAKeysEntity oldRSAKeysEntity = new RSAKeysEntity(2,
                        currentRSAKeysEntity.getPrivate_key(),
                        currentRSAKeysEntity.getPublic_key());
                // Save the new key (kid 2) in the database
                RSAKeysRepository.save(oldRSAKeysEntity);
                RSAKeysRepository.flush();

                // Update the existing key (kid 1) with the new key data
                currentRSAKeysEntity.setPrivate_key(newKeyPair.getPrivate().getEncoded());
                currentRSAKeysEntity.setPublic_key(newKeyPair.getPublic().getEncoded());
                // Save the updated key (kid 1) in the database
                RSAKeysRepository.save(currentRSAKeysEntity);

                // Debug: Print keys after rotation
                System.out.println("New keys after rotation - Private: " +
                        Arrays.toString(newKeyPair.getPrivate().getEncoded()) + ", Public: " +
                        Arrays.toString(newKeyPair.getPublic().getEncoded()));

                // Update the new kid in the JWTUtil class
                if (jwtUtil.fetchTheCurrentKeyFormDatabase())
                    System.out.println("Key fetched from database");
                else {
                    throw new RuntimeException("No RSA keys found in the database");
                }

            } else {
                // if the database does not have any keys, create a new key pair and store it in the database
                RSAKeysEntity newRSAKeysEntity = new RSAKeysEntity(1,
                        newKeyPair.getPrivate().getEncoded(),
                        newKeyPair.getPublic().getEncoded());
                RSAKeysRepository.save(newRSAKeysEntity);
            }
        } catch (NoSuchAlgorithmException e) {
            // Handle exception
        }
    }
}
