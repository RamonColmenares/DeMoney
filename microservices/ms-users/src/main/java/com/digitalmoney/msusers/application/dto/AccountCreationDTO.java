package com.digitalmoney.msusers.application.dto;

public record AccountCreationDTO(
        Long id,
        Long user_id,
        String cvu,
        String alias
) {}
