package com.digitalmoney.msusers.application.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public record UserRegisterResponseDTO
        (Long id,
         String firstName,
         String lastName,
         String dni,
         String email,
         String phone,
         String cvu,
         String alias) {}