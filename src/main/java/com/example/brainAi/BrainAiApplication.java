package com.example.brainAi;

import com.example.brainAi.entity.Role;
import com.example.brainAi.entity.User;
import com.example.brainAi.repository.RoleRepository;
import com.example.brainAi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class BrainAiApplication implements CommandLineRunner {
    final private RoleRepository roleRepository;
    final private UserRepository userRepository;
    final private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(BrainAiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}