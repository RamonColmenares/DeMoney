package com.digitalmoney.msaccounts.application.dto;

import java.math.BigDecimal;

public record TransferenceRequest (
        BigDecimal transactionAmount,
        String destination
){}