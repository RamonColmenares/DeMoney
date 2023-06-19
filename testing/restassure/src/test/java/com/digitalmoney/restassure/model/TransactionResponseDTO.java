package com.digitalmoney.restassure.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
        Long accountId,
        BigDecimal transactionAmount,
        LocalDateTime transactionDate,
        String transactionDescription,
        String destinationCvu,
        Long transactionId,
        String originCvu,
        TransactionActivityDTO.TransactionType transactionType
) {
}
