package com.example.brainAi.service;

import com.example.brainAi.dto.UserDTO;
import com.example.brainAi.entity.Role;
import com.example.brainAi.entity.User;
import com.example.brainAi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO mapUserToUserDTO(User user) {
        if (user != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setEmail(user.getEmail());
            userDTO.setPassword(user.getPassword());
            return userDTO;
        }
        return null;
    }

    // Mapping from DoctorDTO to Doctor entity
    public User mapUserDTOToUser(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        return user;
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        // Check if the email is already in use
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        // Create a new User entity for the Doctor
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.getRoles().add(new Role("ROLE_USER"));
        userRepository.save(user);
        return mapUserToUserDTO(user);
    }
}
