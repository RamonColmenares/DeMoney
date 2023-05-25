package com.digitalmoney.msaccounts.application.dto;

import java.util.Date;

public record CardDTO (

    String cardNumber,
    String cardHolder,
    Date expirationDate,
    String cvv

    ){}
