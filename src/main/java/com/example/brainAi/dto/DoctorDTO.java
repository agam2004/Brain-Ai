package com.example.brainAi.dto;

import jakarta.persistence.Column;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DoctorDTO {

    @NotNull(message = "doctor id should not be null")
    private Long idDoctor;

    @NotNull(message = "first name should not be null")
    @NotBlank(message = "first name should not be blank")
    @NotEmpty(message = "first name should not be empty")
    private String firstName;

    @NotNull(message = "last name should not be null")
    @NotBlank(message = "last name should not be blank")
    @NotEmpty(message = "last name should not be empty")
    private String lastName;

    @NotNull(message = "email should not be null")
    @NotBlank(message = "email should not be blank")
    @NotEmpty(message = "email should not be empty")
    @Email(message = "email should be valid")
    @Column(unique = true, nullable = false, length = 70)
    private String email;

    @NotNull(message = "password should not be null")
    @NotBlank(message = "password should not be blank")
    @NotEmpty(message = "password should not be empty")
    private String password;
}
