package com.example.brainAi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // JwtUtil class for handling JWT token processing and validation
    final private JwtUtil jwtUtil;
    // Custom user details service for loading user-specific data
    final private UserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Retrieve the Authorization header from the request
        String header = request.getHeader(JwtProperties.HEADER_STRING);
        String token;

        // Extract the token from the header if it's present
        if (header != null && header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            token = header.substring(JwtProperties.TOKEN_PREFIX.length());
        } else {
            // Alternatively, try to get the token from a query parameter
            token = request.getParameter("token");
        }

        if (token != null) {
            // Extract the username from the token
            String username = jwtUtil.extractUsername(token);
            // Load user details using the extracted username
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            // Validate the token with the loaded user details
            if (jwtUtil.validateToken(token, userDetails)) {
                // Create an authentication object and set it in the Security Context
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continue with the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
