package com.example.brainAi.service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {
    // A concurrent hash map to store blacklisted tokens and their expiration dates
    private ConcurrentHashMap<String, Instant> blacklist = new ConcurrentHashMap<>();

    // Method to add a token to the blacklist along with its expiration date
    public void addToBlacklist(String token) {
        blacklist.put(token, Instant.now().plusMillis(10000));
    }

    // Method to check if a token is blacklisted
    public boolean isBlacklisted(String token) {
        // Get the token's expiration date from the blacklist
        Instant expiration = blacklist.get(token);

        // If the token is not found in the blacklist, return false (not blacklisted)
        if (expiration == null) {
            return false;
        }

        // If the token's expiration date has passed, remove it from the blacklist and return false (not blacklisted)
        if (Instant.now().isAfter(expiration)) {
            blacklist.remove(token);
            return false;
        }

        // If the token is found and not expired, return true (blacklisted)
        return true;
    }
}
