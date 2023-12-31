package com.digitalmoney.msaccounts.persistency.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferenceRequest (
        BigDecimal transactionAmount,
        String originCvu,
        Long cardId
){}
