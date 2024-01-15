package com.example.brainAi.config;

import com.example.brainAi.entity.Role;
import com.example.brainAi.entity.User;
import io.jsonwebtoken.ClaimsMutator;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Signature;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private static String SECRET_KEY = JwtProperties.SECRET;
    private long TOKEN_VALIDITY = JwtProperties.EXPIRATION_TIME;

    public String generateToken(AuthenticationRequest authenticationRequest, UserDetails userDetails) {
        User user = new User();
        user.setEmail(authenticationRequest.getUsername());
        user.setPassword(authenticationRequest.getPassword());
        user.setRoles(userDetails.getAuthorities().stream().map(authority -> new Role(null, authority.getAuthority(),
                null)).collect(Collectors.toList()));

        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        claims.put("roles",
                user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));

        String message = "";
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
        return token;
    }

    public Instant getExpirationDateFromToken(String token) {
        Date expirationDate = extractClaim(token, Claims::getExpiration);
        return expirationDate.toInstant();
    }

    public static boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private static Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
