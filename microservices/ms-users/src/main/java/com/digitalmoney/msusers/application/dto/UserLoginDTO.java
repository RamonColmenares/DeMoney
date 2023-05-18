package com.digitalmoney.msusers.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDTO {
    @NotBlank
    String email;
    @NotBlank
    String password;
}
