package com.digitalmoney.msaccounts.application.dto;

public record UserDTO (
        Long id,
        String name,
        String last_name,
        String dni,
        String email,
        String phone,
        AccountDTO accountDTO
){}
