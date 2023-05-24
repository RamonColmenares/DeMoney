package com.digitalmoney.msaccounts.persistency.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Entity(name = "accounts") @Data
public class Account {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank(message = "CVU is mandatory")
        @Column(name = "user_id", nullable = false, unique = true)
        private Long userId;

        @NotBlank(message = "CVU is mandatory")
        @Size(min = 22, max = 22, message = "CVU must be 22 characters long")
        @Column(name = "cvu", nullable = false, unique = true)
        private String cvu;

        @NotBlank(message = "Alias is mandatory")
        @Column(name = "alias", nullable = false, unique = true)
        private String alias;
    }
