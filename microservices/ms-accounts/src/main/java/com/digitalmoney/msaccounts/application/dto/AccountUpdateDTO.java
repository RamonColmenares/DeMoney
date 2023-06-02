package com.digitalmoney.msaccounts.application.dto;

import jakarta.validation.constraints.NotBlank;

public record AccountUpdateDTO(
        @NotBlank(message = "Alias is mandatory") String alias
        ){}
