package com.example.brainAi.config;

import com.example.brainAi.util.RSAKeyGenerator;
import org.springframework.context.annotation.Bean;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class RSAConfiguration {
    @Bean
    public KeyPair rsaKeyPair() {
        try {
            RSAKeyGenerator rsaKeyGenerator = new RSAKeyGenerator();
            return rsaKeyGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to generate RSA key pair", e);
        }
    }
}
