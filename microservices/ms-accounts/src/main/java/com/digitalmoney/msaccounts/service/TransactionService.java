package com.digitalmoney.msaccounts.service;

import com.digitalmoney.msaccounts.application.exception.BadRequestException;
import com.digitalmoney.msaccounts.persistency.dto.TransactionResponseDTO;
import com.digitalmoney.msaccounts.persistency.dto.TransferenceRequest;
import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import com.digitalmoney.msaccounts.persistency.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service @AllArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;

    public Page<TransactionResponseDTO> getTransactionByAccountId(Long idAccount, int page, int size) {
        return repository.getTransactionByAccountId(idAccount, PageRequest.of(page, size, Sort.by("transactionDate")))
                .map(this::convertToDto);
    }

    public TransactionResponseDTO createTransactionFromCard(TransferenceRequest transferenceRequest, Account account) throws BadRequestException {

        if (transferenceRequest.transactionAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero.");
        }

        LocalDateTime date = LocalDateTime.now();

        Transaction transaction = new Transaction();

        transaction.setAmount(transferenceRequest.transactionAmount());
        transaction.setTransactionDate(date);
        transaction.setDestinationCvu(transferenceRequest.destinationCvu());
        transaction.setOriginCvu(transferenceRequest.originCvu());
        transaction.setAccount(account);
        transaction.setTransactionDescription("Deposit from credit/debit card.");
        transaction.setTransactionType(Transaction.TransactionType.income);

        repository.save(transaction);

        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO(
                account.getId(),
                transferenceRequest.transactionAmount(),
                date,
                "Deposit from credit/debit card.",
                transferenceRequest.destinationCvu(),
                transaction.getId(),
                transferenceRequest.originCvu(),
                Transaction.TransactionType.income
        );

        return transactionResponseDTO;

    }

    private TransactionResponseDTO convertToDto(Transaction transaction) {
        // Convert Transaction to TransactionDTO
        return new TransactionResponseDTO(
                transaction.getAccount().getId(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getTransactionDescription(),
                transaction.getDestinationCvu(),
                transaction.getId(),
                transaction.getOriginCvu(),
                transaction.getTransactionType()
        );
    }
}
