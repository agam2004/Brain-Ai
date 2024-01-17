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
         /*
        at first use of the app, if no user table exists, then create admin user with:
        'admin@gmail.com' username and 'admin' password
                */

        Role role = roleRepository.findByName("ROLE_USER");
        if (role == null) {
            User user = new User("admin", "user", "admin@gmail.com",
                    passwordEncoder.encode("admin"),
                    List.of(new Role("ROLE_ADMIN")));
            userRepository.save(user);
            user = new User("user", "user", "user@gmail.com",
                    passwordEncoder.encode("user"),
                    List.of(new Role("ROLE_USER")));
            userRepository.save(user);
        }
    }

}