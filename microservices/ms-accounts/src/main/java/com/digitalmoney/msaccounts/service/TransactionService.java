package com.digitalmoney.msaccounts.service;

import com.digitalmoney.msaccounts.application.exception.BadRequestException;
import com.digitalmoney.msaccounts.application.exception.NotFoundException;
import com.digitalmoney.msaccounts.persistency.dto.TransactionResponseDTO;
import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import com.digitalmoney.msaccounts.persistency.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service @AllArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;

    public Page<TransactionResponseDTO> getTransactionByAccountId(Long idAccount, int page, int size) {
        return repository.getTransactionByAccountId(idAccount, PageRequest.of(page, size, Sort.by("transactionDate")))
                .map(this::convertToDto);
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

    public Transaction getTransactionDetail(Long id, Long transactionID) throws NotFoundException, BadRequestException {
        Transaction transaction = repository.findById(transactionID).orElse(null);
        if (transaction == null) {
            throw new NotFoundException("Transaction doesnt exist.");
        }
        if (!Objects.equals(transaction.getAccount().getId(), id)) {
            throw new BadRequestException("Transaction doesnt belong to the account.");
        }

        return transaction;
    }
}
