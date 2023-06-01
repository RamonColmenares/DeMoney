package com.digitalmoney.msaccounts.service;

import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import com.digitalmoney.msaccounts.persistency.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service @AllArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;

    public Page<Transaction> getTransactionByAccountId(Long idAccount, int page, int size) {
        return repository.getTransactionByAccountId(idAccount, PageRequest.of(page, size));
    }
}
