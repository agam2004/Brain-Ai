package com.example.brainAi.config;

import com.example.brainAi.entity.Role;
import com.example.brainAi.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    // Secret key for signing JWT tokens
    private String SECRET_KEY = JwtProperties.SECRET;
    // Token validity duration in milliseconds (10 hours)
    private long TOKEN_VALIDITY = JwtProperties.EXPIRATION_TIME;

    // Generate a JWT token for a user
    public String generateToken(AuthenticationRequest authenticationRequest, UserDetails userDetails) {
        // If the credentials are valid, generate an access token using JwtUtil
        User user = new User();
        user.setEmail(authenticationRequest.getUsername());
        user.setPassword(authenticationRequest.getPassword());
        user.setRoles(userDetails.getAuthorities().stream().map(authority -> new Role(null, authority.getAuthority(),
                null)).collect(Collectors.toList()));

        // Create claims and set the subject (username)
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        // Add the user's roles to the claims
        claims.put("roles",
                user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));

        String message = "";
        // Build and sign the JWT token
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        return token;
        // return "Bearer " + token;
    }

    // Get the expiration date of the token as an Instant
    public Instant getExpirationDateFromToken(String token) {
        Date expirationDate = extractClaim(token, Claims::getExpiration);
        return expirationDate.toInstant();
    }

    // Validate the JWT token for a userDetails
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Extract the username (subject) from the JWT token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract a claim from the JWT token using a claims resolver function
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the JWT token
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    // Check if the JWT token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract the expiration date from the JWT token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
