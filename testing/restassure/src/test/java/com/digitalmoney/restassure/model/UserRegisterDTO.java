package com.digitalmoney.restassure.model;

public record UserRegisterDTO
        (String firstName,
         String lastName,
         String dni,
         String email,
         String phone,
         String password) {
}
