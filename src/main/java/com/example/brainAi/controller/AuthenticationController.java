package com.example.brainAi.controller;

import com.example.brainAi.dto.AuthenticationRequest;
import com.example.brainAi.dto.AuthenticationResponse;
import com.example.brainAi.dto.RefreshTokenRequest;
import com.example.brainAi.exceptions.AuthenticationServiceException;
import com.example.brainAi.exceptions.TokenRefreshException;
import com.example.brainAi.service.AuthenticationService;
import com.example.brainAi.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

@Controller
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;


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


    // The authenticateUser() method takes in an AuthenticationRequest object, which contains the username and password.
    // The method returns an AuthenticationResponse object, which contains the JWT and refresh token, and the user's roles.
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            // authenticate() method throws an AuthenticationServiceException if the username or password is invalid.
            AuthenticationResponse authResponse = authenticationService.authenticate(authenticationRequest);

            // print out the tokens, roles, and username, for testing
            System.out.println("refresh token: " + authResponse.getRefreshToken());
            System.out.println("jwt token: " + authResponse.getAccessToken());
            System.out.println("roles: " + authResponse.getRoles());
            System.out.println("username: " + authenticationRequest.getEmail());

            // if the username and password are valid, the method returns an AuthenticationResponse object OK response.
            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationServiceException e) {
            // if the username or password is invalid, the method returns a 401 Unauthorized response.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /*
    @PostMapping('/register')
    public ResponseEntity<?> registerUser(@RequestBody String firstName, String lastName, String email, String password) {
        try {

            return ResponseEntity.status(HttpStatus.OK).body("Registration successful");
        } catch (AuthenticationServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
     */

    // The refreshToken() method takes in a RefreshTokenRequest object, which contains the refresh token.
    // The method returns an AuthenticationResponse object, which contains the JWT and refresh token, and the user's roles.
    @PostMapping("/refresh_token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            // try to refresh the token, if the refresh token is valid, the method returns an AuthenticationResponse object OK response.
            AuthenticationResponse authResponse = refreshTokenService.refresh(refreshTokenRequest.getRefreshToken());
            System.out.println("refresh token: " + authResponse.getRefreshToken());
            System.out.println("jwt token: " + authResponse.getAccessToken());
            return ResponseEntity.ok(authResponse);
        } catch (TokenRefreshException e) {
            // else, the method returns a 401 Unauthorized response.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
