package com.digitalmoney.msaccounts.controller;

import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import com.digitalmoney.msaccounts.service.AccountService;
import com.digitalmoney.msaccounts.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
public class AccountController {
    private final AccountService service;
    private final TransactionService transactionService;

    @GetMapping("/test-db")
    public List<Account> testDb() {
        return service.findAll();
    }

    @GetMapping("{id}/transactions")
    public Page<Transaction> getMyTransactions(@PathVariable Long id, @RequestParam int page, @RequestParam int size) {
        return transactionService.getTransactionByAccountId(id, page, size);
    }

}
