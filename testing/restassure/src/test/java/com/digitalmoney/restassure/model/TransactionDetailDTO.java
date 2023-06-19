package com.digitalmoney.restassure.model;

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
    private TransactionActivityDTO.TransactionType transactionType;
}
