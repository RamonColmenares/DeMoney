package com.digitalmoney.msaccounts.application.dto;

import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionDetailDTO {
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String transactionDescription;
    private String destinationCvu;
    private String originCvu;
    private Transaction.TransactionType transactionType;
}
