package com.digitalmoney.msusers.application.dto;

public record UserUpdateResponseDTO
        (Long id,
         String firstName,
         String lastName,
         String dni,
         String email,
         String phone) {}