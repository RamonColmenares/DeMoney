package com.digitalmoney.msusers.application.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterResponseDTO {
    private String firstName;
    private String lastName;
    private String dni;
    private String email;
    private String phone;
    private String cvu;
    private String alias;
}
