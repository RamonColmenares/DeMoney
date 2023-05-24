package com.digitalmoney.msusers.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

public record UserLoginDTO (@NotBlank(message = "Email mustn't be empty.") String email, @NotBlank(message = "Password mustn't be empty.") String password) {}
