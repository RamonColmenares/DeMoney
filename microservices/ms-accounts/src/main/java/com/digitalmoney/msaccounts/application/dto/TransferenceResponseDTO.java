package com.digitalmoney.msaccounts.application.dto;

import com.digitalmoney.msaccounts.persistency.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferenceResponseDTO (
        Long id,
        BigDecimal transactionAmount,
        LocalDateTime transactionDate,
        String transactionDescription,
        String destination,
        Long transactionId,
        String originCvu,
        Transaction.TransactionType transactionType
){}
