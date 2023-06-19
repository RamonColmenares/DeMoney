package com.digitalmoney.restassure.model;

import java.util.Date;

public record CardResponseDTO
        (String cardNumber,
        String cardHolder,
        Date expirationDate) {}
