package com.digitalmoney.msaccounts.persistency.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Entity(name = "cards") @Data
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Card number is mandatory")
    @Column(name = "card_number", unique = true, length = 16)
    private String cardNumber;

    @NotNull(message = "Card holder is mandatory")
    @Column(name = "cardholder")
    private String cardHolder;

    @NotNull(message = "Expiration date is mandatory")
    @Column(name = "expiration_date")
    private Date expirationDate;

    @NotNull(message = "CVV is mandatory")
    @Column(name = "cvv", length = 3)
    private String cvv;

    @NotNull(message = "Account is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

}
