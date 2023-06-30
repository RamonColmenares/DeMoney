package com.digitalmoney.msaccounts.persistency.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @NotNull
    @DecimalMin(value = "0.0")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    @NotNull
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @NotNull
    @Size(max = 255)
    @Column(name = "transaction_description")
    private String transactionDescription;

    @NotNull
    @Size(min = 3, max = 22)
    @Column(name = "destination_cvu")
    private String destination;

    @NotNull
    @Size(min = 22, max = 22)
    @Column(name = "origin_cvu")
    private String originCvu;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;

    // Enums
    public enum TransactionType {
        income, expense
    }

    public Transaction cloneAsIncome() {
        Transaction clone = new Transaction();
        clone.setAccount(this.getAccount());
        clone.setAmount(this.getAmount());
        clone.setTransactionDate(this.getTransactionDate());
        clone.setTransactionDescription(this.getTransactionDescription());
        clone.setDestination(this.getDestination());
        clone.setOriginCvu(this.getOriginCvu());
        clone.setTransactionType(Transaction.TransactionType.income);
        return clone;
    }
}
