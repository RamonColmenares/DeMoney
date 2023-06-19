package com.digitalmoney.restassure.model;

public record AccountResponseDTO
        (Long id,
        Long userId,
        String cvu,
        String alias,
        Double balance){}

