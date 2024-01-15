package com.example.brainAi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class LoginDTO {
    @NotNull(message = "username should not be null")
    @NotBlank(message = "username should not be blank")
    @NotEmpty(message = "username should not be empty")
    private String email;

    @NotNull(message = "password should not be null")
    @NotBlank(message = "password should not be blank")
    @NotEmpty(message = "password should not be empty")
    private String password;
}
