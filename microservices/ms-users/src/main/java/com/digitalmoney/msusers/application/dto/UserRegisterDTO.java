package com.digitalmoney.msusers.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterDTO
        (@NotBlank(message = "First name is mandatory") String firstName,
        @NotBlank(message = "Last name is mandatory") String lastName,
        @NotBlank(message = "DNI is mandatory")
        @Size(min = 3, max = 9, message = "DNI must be between 8 and 50 characters") String dni,
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email should be valid") String email,
        @NotBlank(message = "Phone is mandatory") String phone/*,
        @NotBlank(message = "Password is mandatory") String password*/) {}
