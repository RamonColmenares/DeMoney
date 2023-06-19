package com.digitalmoney.restassure.model;

import java.util.Date;

public record CardDTO (

    String cardNumber,
    String cardHolder,
    Date expirationDate,
    String cvv

    ){}
