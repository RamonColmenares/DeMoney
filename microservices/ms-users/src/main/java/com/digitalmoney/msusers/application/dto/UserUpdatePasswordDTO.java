package com.digitalmoney.msusers.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UserUpdatePasswordDTO (@NotBlank(message = "Email mustn't be empty.") String email, @NotBlank(message = "Password mustn't be empty.") String password) {}
