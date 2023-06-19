package com.digitalmoney.restassure.model;

public record UserUpdateResponseDTO
        (Long id,
         String firstName,
         String lastName,
         String dni,
         String email,
         String phone) {}