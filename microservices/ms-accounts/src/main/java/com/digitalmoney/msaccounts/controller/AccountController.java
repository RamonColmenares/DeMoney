package com.digitalmoney.msaccounts.controller;

import com.digitalmoney.msaccounts.application.dto.AccountUpdateDTO;
import com.digitalmoney.msaccounts.application.dto.UserAccountDTO;
import com.digitalmoney.msaccounts.application.exception.AccountBadRequestException;
import com.digitalmoney.msaccounts.application.exception.AccountInternalServerException;
import com.digitalmoney.msaccounts.application.exception.AccountNotFoundException;
import com.digitalmoney.msaccounts.application.exception.AccountUnauthorizedException;
import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.service.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService service;

    @GetMapping("/test-db")
    public List<Account> testDb() {
        return service.findAll();
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody UserAccountDTO user) {
        try {
            return ResponseEntity.ok(service.createAccount(user));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findByID(@PathVariable String id) throws AccountNotFoundException, AccountBadRequestException, AccountUnauthorizedException {
        return ResponseEntity.ok().body(service.findAccountByUserID(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody @Valid AccountUpdateDTO account) throws AccountNotFoundException, AccountBadRequestException, AccountUnauthorizedException, AccountInternalServerException {
        return ResponseEntity.ok().body(service.updateAccount(id, account));
    }

}
