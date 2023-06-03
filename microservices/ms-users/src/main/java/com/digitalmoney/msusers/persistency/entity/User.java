package com.digitalmoney.msusers.persistency.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity(name = "users") @Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is mandatory")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "DNI is mandatory")
    @Size(min = 3, max = 9, message = "DNI must be between 8 and 50 characters")
    @Column(name = "dni", nullable = false, unique = true)
    private String dni;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Phone is mandatory")
    @Column(name = "phone", nullable = false)
    private String phone;

    @NotBlank(message = "Password is mandatory")
    @Column(name = "password", nullable = false)
    private String password;
}
