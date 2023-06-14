package com.digitalmoney.msaccounts.persistency.dto;

import com.digitalmoney.msaccounts.persistency.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDTO (Long accountId,
                                      BigDecimal transactionAmount,
                                      LocalDateTime transactionDate,
                                      String transactionDescription,
                                      String destinationCvu,
                                      Long transactionId,
                                      String originCvu,
                                      Transaction.TransactionType transactionType
) {
}
