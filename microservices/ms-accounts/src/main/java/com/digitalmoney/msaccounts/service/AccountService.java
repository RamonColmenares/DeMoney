package com.digitalmoney.msaccounts.service;

import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.persistency.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @AllArgsConstructor
public class AccountService {
    private final AccountRepository repository;

    public List<Account> findAll() {
        return repository.findAll();
    }
}
