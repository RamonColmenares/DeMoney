package com.digitalmoney.msaccounts.controller;

import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
public class AccountController {
    private final AccountService service;

    @GetMapping("/test-db")
    public List<Account> testDb() {
        return service.findAll();
    }

}
