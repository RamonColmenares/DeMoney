package com.digitalmoney.msaccounts.controller;

import com.digitalmoney.msaccounts.persistency.dto.TransactionResponseDTO;
import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import com.digitalmoney.msaccounts.service.AccountService;
import com.digitalmoney.msaccounts.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("{id}/transactions/all")
    public Page<TransactionResponseDTO> getAllMyTransactions(@PathVariable Long id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        return transactionService.getTransactionByAccountId(id, page, size);
    }

    @GetMapping("{id}/transactions")
    public List<TransactionResponseDTO> getMyLastFiveTransactions(@PathVariable Long id) {
        return getAllMyTransactions(id, 0, 5).getContent();
    }

}
