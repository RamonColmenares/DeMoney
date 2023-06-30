package com.digitalmoney.restassure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data @AllArgsConstructor @NoArgsConstructor
public class CardAddResponseDTO {

    private Long id;
    private String cardNumber;
    private String cardHolder;
    private Date expirationDate;
    private String cvv;
    private AccountResponseDTO account;

}
