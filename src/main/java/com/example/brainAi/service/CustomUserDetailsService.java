package com.example.brainAi.service;

import com.example.brainAi.entity.Role;
import com.example.brainAi.entity.User;
import com.example.brainAi.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find the user by email in the database using the UserRepository injected into the service
        User user = userRepository.findByEmail(email);

        // If the user is found in the database, create a UserDetails object with their email, password, and roles
        if (user != null) {
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(), // email as the username
                    user.getPassword(), // hashed password
                    mapRolesToAuthorities(user.getRoles()) // map the user's roles to Spring Security's GrantedAuthorities
            );
        } else { // If the user is not found in the database, throw an exception
            throw new UsernameNotFoundException("Invalid username or password.");
        }
    }

    // Helper method to map the user's roles to Spring Security's GrantedAuthorities
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
