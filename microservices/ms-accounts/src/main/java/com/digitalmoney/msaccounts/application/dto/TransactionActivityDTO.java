package com.digitalmoney.msaccounts.application.dto;

import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @AllArgsConstructor
public class TransactionActivityDTO {
    private BigDecimal transactionAmount;
    private LocalDateTime transactionDate;
    private Integer id;
    private Transaction.TransactionType transactionType;
}
