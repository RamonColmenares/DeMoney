package com.digitalmoney.msaccounts.application.dto;

import java.util.Date;

public record CardResponseDTO
        (String cardNumber,
        String cardHolder,
        Date expirationDate) {}
