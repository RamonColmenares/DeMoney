package com.digitalmoney.restassure.model;

import java.math.BigDecimal;

public record TransferenceRequest(
        BigDecimal transactionAmount,
        String destinationCvu,
        String originCvu,
        Long cardId
){}
