package com.example.brainAi.service;

import com.example.brainAi.util.JwtProperties;
import com.example.brainAi.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    final private TokenBlacklistService tokenBlacklistService;
    final private RefreshTokenService refreshTokenService;
    final private JwtUtil jwtUtil; // assuming you have a JwtUtil class to decode the JWT


    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        // TODO made changes here, CCustomLogoutSuccessHandler for react app
        // Extract the Authorization header
        String authHeader = request.getHeader(JwtProperties.HEADER_STRING);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extract the JWT token
            String token = authHeader.substring("Bearer ".length());
            tokenBlacklistService.addToBlacklist(token);

            // Decode the JWT to get the email
            String email = jwtUtil.extractUsername(token);

            if (email == null) {
                // Handle error: the token is invalid
                // Log or handle as appropriate for your application
            }

            // Delete the refresh token for the user
            refreshTokenService.deleteRefreshTokenForUser(email);
            System.out.println("user logged out: " + email);
        } else {
            // Handle error: the token was not found in the request
            // Log or handle as appropriate for your application
        }

        // Redirect to the login page or other logic
        // TODO: redirect to login page not needed if using JWT from the frontend react app
        //  response.sendRedirect("/index");
    }
}
