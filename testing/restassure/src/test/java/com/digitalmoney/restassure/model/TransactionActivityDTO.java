package com.digitalmoney.restassure.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @AllArgsConstructor
public class TransactionActivityDTO {
    private BigDecimal transactionAmount;
    private LocalDateTime transactionDate;
    private Long transactionId;
    private TransactionType transactionType;

    public enum TransactionType {
        income, expense
    }
}
