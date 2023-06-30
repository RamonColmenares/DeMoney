package com.digitalmoney.msusers.application.dto;

public record UserResponseDTO
        (Long id,
         String firstName,
         String lastName,
         String dni,
         String email,
         String phone,
         AccountDTO account) {}