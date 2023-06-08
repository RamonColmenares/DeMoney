package com.digitalmoney.msaccounts.persistency.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferenceRequest (
        BigDecimal transactionAmount,
        LocalDateTime transactionDate,
        String destinationCvu,
        String originCvu
){}
