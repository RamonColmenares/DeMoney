package com.digitalmoney.restassure.model;

public record UserResponseDTO
        (Long id,
         String firstName,
         String lastName,
         String dni,
         String email,
         String phone,
         AccountDTO account) {}