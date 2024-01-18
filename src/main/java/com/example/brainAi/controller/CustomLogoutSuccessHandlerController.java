package com.example.brainAi.controller;

import com.example.brainAi.service.TokenBlacklistService;
import com.example.brainAi.util.JwtProperties;
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
public class CustomLogoutSuccessHandlerController implements LogoutSuccessHandler {
    final private TokenBlacklistService tokenBlacklistService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        // Extract the JWT token
        String token = request.getHeader(JwtProperties.HEADER_STRING).substring(JwtProperties.HEADER_STRING.length());
        tokenBlacklistService.addToBlacklist(token);

        // Redirect to the login page or other logic
        response.sendRedirect("/index");
    }
}
