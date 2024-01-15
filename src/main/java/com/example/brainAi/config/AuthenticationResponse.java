package com.example.brainAi.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticationResponse {

    private String accessToken;
    private List<String> roles;

    public AuthenticationResponse(String accessToken, Collection<? extends GrantedAuthority> roles) {
        this.accessToken = accessToken;
        this.roles = roles.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }
    public String getJwtToken() {
        return this.accessToken;
    }
}
