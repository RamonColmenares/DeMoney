package com.digitalmoney.msusers.persistency.entity;

import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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

    @NotBlank(message = "CVU is mandatory")
    @Size(min = 22, max = 22, message = "CVU must be 22 characters long")
    @Column(name = "cvu", nullable = false, unique = true)
    private String cvu;

    @NotBlank(message = "Alias is mandatory")
    @Column(name = "alias", nullable = false, unique = true)
    private String alias;
}
