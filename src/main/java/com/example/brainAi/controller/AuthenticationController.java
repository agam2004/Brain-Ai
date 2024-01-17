package com.example.brainAi.controller;

import com.example.brainAi.config.AuthenticationRequest;
import com.example.brainAi.config.AuthenticationResponse;
import com.example.brainAi.service.CustomUserDetailsService;
import com.example.brainAi.service.TokenBlacklistService;
import com.example.brainAi.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

@Controller
public class AuthenticationController {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthenticationController(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService,
                                    PasswordEncoder passwordEncoder, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.tokenBlacklistService = tokenBlacklistService;
    }


    @GetMapping("/index")
    public String index() {
        return "login";
    }


    @GetMapping("/home")
    public String Home() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        System.out.println("Logged in user: " + username);

        // Your redirection logic based on roles
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin_home";
        } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            return "redirect:/user_home";
        }

        return "redirect:/index";
    }

    @GetMapping("/user_home")
    public String useHome() {
        return "user_home";
    }

    @GetMapping("/admin_home")
    public String useAdmin() {
        return "admin_home";
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest authenticationRequest) {
        UserDetails userDetails = null;
        try {
            userDetails = customUserDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        // Validate the password (replace with your own validation logic)
        if (passwordEncoder.matches(authenticationRequest.getPassword(), userDetails.getPassword())) {
            String jwtToken = jwtUtil.generateToken(authenticationRequest, userDetails);

            // Convert the authorities to a list of Role objects
            AuthenticationResponse authResponse = getAuthenticationResponse(userDetails, jwtToken);
            return ResponseEntity.ok(authResponse);
        } else {
            // If the credentials are invalid, return an unauthorized status
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    private static AuthenticationResponse getAuthenticationResponse(UserDetails userDetails, String jwtToken) {
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();

            /* Retrieve the user's roles from the UserDetails object
            and set them in the User object
             */
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // Convert the authorities to a list of Role objects
        // Create and return the AuthenticationResponse with the JWT token and roles
        AuthenticationResponse authResponse = new AuthenticationResponse(jwtToken, roles);
        return authResponse;
    }
}
