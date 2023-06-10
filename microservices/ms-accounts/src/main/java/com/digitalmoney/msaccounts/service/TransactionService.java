package com.digitalmoney.msaccounts.service;

import com.digitalmoney.msaccounts.application.exception.BadRequestException;
import com.digitalmoney.msaccounts.application.exception.InternalServerException;
import com.digitalmoney.msaccounts.persistency.dto.TransactionResponseDTO;
import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import com.digitalmoney.msaccounts.persistency.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service @AllArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;

    public Page<TransactionResponseDTO> getTransactionByAccountId(Long idAccount, int page, int size) {
        return repository.getTransactionByAccountId(idAccount, PageRequest.of(page, size, Sort.by("transactionDate")))
                .map(this::convertToDto);
    }

    public TransactionResponseDTO getTransactionByAmount(Long idAccount, int min, int max) throws BadRequestException, InternalServerException {
        if (min < 0) {
            throw new BadRequestException("the minimum amount to search is 0");
        }

        Transaction transaction;
        try {
            transaction = repository.getTransactionByAmount(idAccount, min, max);
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }

        return convertToDto(transaction);
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
