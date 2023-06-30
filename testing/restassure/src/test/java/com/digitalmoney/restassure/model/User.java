package com.digitalmoney.restassure.model;

import lombok.Data;

@Data
public class User {

    private Long id;
    private String firstName;
    private String lastName;
    private String dni;
    private String email;
    private String phone;
    private String password;
    private String hash;
    private UserStatus status;
    public enum UserStatus {
        active, inactive, pending, expired
    }
}
