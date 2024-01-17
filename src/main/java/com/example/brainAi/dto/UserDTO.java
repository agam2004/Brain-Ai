package com.example.brainAi.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserDTO {
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
