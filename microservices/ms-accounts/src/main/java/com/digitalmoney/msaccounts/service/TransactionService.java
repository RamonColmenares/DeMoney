package com.digitalmoney.msaccounts.service;

import com.digitalmoney.msaccounts.application.exception.InternalServerException;
import com.digitalmoney.msaccounts.persistency.dto.TransactionResponseDTO;
import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import com.digitalmoney.msaccounts.persistency.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service @AllArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;

    public List<Transaction> findAll(){
        return repository.findAll();
    }

    public Page<TransactionResponseDTO> getTransactionByAccountId(Long idAccount, int page, int size) {
        return repository.getTransactionByAccountId(idAccount, PageRequest.of(page, size, Sort.by("transactionDate")))
                .map(this::convertToDto);
    }

    public List<TransactionResponseDTO> getTransactionsByAccountIdAndDateRange(Long idAccount, LocalDate startDate, LocalDate endDate) throws InternalServerException {
        LocalDateTime startDateTime = startDate.atTime(LocalTime.MIDNIGHT);
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Transaction> transactions;
        try {
            transactions = repository.getTransactionByAccountIdAndDateRange(idAccount, startDateTime, endDateTime);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
        return transactions.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<TransactionResponseDTO> getTransactionsByAccountIdAndTransactionType(Long idAccount, String transactionTypeString) throws InternalServerException {
        Transaction.TransactionType transactionType = Transaction.TransactionType.valueOf(transactionTypeString);

        List<Transaction> transactions;
        try {
            transactions = repository.getTransactionsByAccountIdAndTransactionType(idAccount, transactionType);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
        return transactions.stream().map(this::convertToDto).collect(Collectors.toList());
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
