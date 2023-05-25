package com.digitalmoney.msaccounts.controller;

import com.digitalmoney.msaccounts.application.dto.UserAccountDTO;
import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.service.AccountService;
import com.digitalmoney.msaccounts.service.CardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService service;

    private final CardService cardService;

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

    @GetMapping("/{accountId}/cards")
    public ResponseEntity<?> getAllCardsByAccountId(@PathVariable Long accountId){
        return null;
    }

    @GetMapping("/{accountId}/cards/{cardId}")
    public ResponseEntity<?> getCardByIdAndAccountId(@PathVariable Long accountId, @PathVariable Long cardId) {
        return null;
    }

}
