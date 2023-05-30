package com.digitalmoney.msaccounts.controller;

import com.digitalmoney.msaccounts.application.dto.CardDTO;
import com.digitalmoney.msaccounts.application.dto.UserAccountDTO;
import com.digitalmoney.msaccounts.application.exception.AlreadyExistsException;
import com.digitalmoney.msaccounts.application.exception.NotFoundException;
import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.persistency.entity.Card;
import com.digitalmoney.msaccounts.service.AccountService;
import com.digitalmoney.msaccounts.service.CardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
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
    @GetMapping("/test-db2")
    public List<Card> testDb2() {
        return cardService.findAll();
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

    @PostMapping("/{accountId}/cards")
    public ResponseEntity<?> addCard(@PathVariable Long accountId, @RequestBody CardDTO cardDTO){
        Card result;
        try {
            result = cardService.addCard(accountId, cardDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(400).build();
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(409).build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{accountId}/cards/{cardId}")
    public ResponseEntity<?> getCardByIdAndAccountId(@PathVariable Long accountId, @PathVariable Long cardId) {
        return null;
    }

}
