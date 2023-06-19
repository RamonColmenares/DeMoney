package com.digitalmoney.restassure.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public record UserUpdateDTO
        (String firstName,
         String lastName,
         @Size(min = 3, max = 9, message = "DNI must be between 8 and 50 characters") String dni,
         @Email(message = "Email should be valid") String email,
         String phone,
         String password) {}