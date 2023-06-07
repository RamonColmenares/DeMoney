package com.digitalmoney.msaccounts.persistency.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity(name = "cards") @Data @AllArgsConstructor @NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Card number is mandatory")
    @Size(min = 14, max = 18, message = "Card number is invalid")
    @Column(name = "card_number", unique = true)
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
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

}
