package com.digitalmoney.msaccounts.controller;

import com.digitalmoney.msaccounts.application.dto.AccountUpdateDTO;
import com.digitalmoney.msaccounts.application.dto.CardDTO;
import com.digitalmoney.msaccounts.application.dto.CardResponseDTO;
import com.digitalmoney.msaccounts.application.dto.UserAccountDTO;
import com.digitalmoney.msaccounts.application.exception.BadRequestException;
import com.digitalmoney.msaccounts.application.exception.InternalServerException;
import com.digitalmoney.msaccounts.application.exception.NotFoundException;
import com.digitalmoney.msaccounts.application.exception.UnauthorizedException;
import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.persistency.entity.Card;
import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import com.digitalmoney.msaccounts.service.AccountService;
import com.digitalmoney.msaccounts.service.CardService;
import com.digitalmoney.msaccounts.service.SecurityService;
import com.digitalmoney.msaccounts.service.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@RestController
public class AccountController {
    private final AccountService service;
    private final TransactionService transactionService;
    private final CardService cardService;
    private final SecurityService securityService;

    @GetMapping("/test-db")
    public List<Account> testDb() {
        return service.findAll();
    }
    @GetMapping("/test-db2")
    public List<Card> testDb2() {
        return cardService.findAll();
    }
    @GetMapping("/test-db3")
    public List<Transaction> testDb3() {
        return transactionService.findAll();
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody UserAccountDTO user) {
        try {
            return ResponseEntity.ok(service.createAccount(user));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> findByUserID(@PathVariable String id) throws NotFoundException, BadRequestException, UnauthorizedException {
        return ResponseEntity.ok().body(service.findAccountByUserID(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findByID(@PathVariable String id) throws NotFoundException, BadRequestException, UnauthorizedException {
        return ResponseEntity.ok().body(service.findAccountByID(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody @Valid AccountUpdateDTO account) throws NotFoundException, BadRequestException, UnauthorizedException, InternalServerException {
        return ResponseEntity.ok().body(service.updateAccount(id, account));
    }
    @GetMapping("/{accountId}/cards")
    public ResponseEntity<?> getAllCardsByAccountId (@PathVariable Long accountId) {
        List<CardResponseDTO> cards = cardService.findAllByAccountId(accountId);
        System.out.println(cards);
        if (cards.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("There are no cards associated with the account.");
        }

        return ResponseEntity.ok().body(cards);
    }

    @GetMapping("/{accountId}/cards/{cardId}")
    public ResponseEntity<?> getCardByIdAndAccountId (@PathVariable Long accountId, @PathVariable Long cardId) throws NotFoundException {
        return ResponseEntity.ok().body(cardService.findByIdAndAccountId(cardId, accountId));
    }

    @PostMapping("/{accountId}/cards")
    public ResponseEntity<?> addCard(@PathVariable Long accountId, @RequestBody CardDTO cardDTO) throws InternalServerException, NotFoundException, BadRequestException {
        return ResponseEntity.ok().body(cardService.addCard(accountId, cardDTO));
    }

    @DeleteMapping("/{accountId}/cards/{cardId}")
    public ResponseEntity<String> deleteCardByIdAndAccountId(@PathVariable Long accountId, @PathVariable Long cardId) {
        try {
            cardService.deleteCardByIdAndAccountId(accountId, cardId);
            return ResponseEntity.ok("Card deleted successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("{id}/activity")
    public ResponseEntity<?> getAllMyTransactions(@PathVariable Long id,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "5") int size,
                                                  @RequestParam(required = false) LocalDate startDate,
                                                  @RequestParam(required = false) LocalDate endDate,
                                                  @RequestParam(required = false) String transactionType) throws InternalServerException {
        if(!securityService.isMyAccount(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account doesnt belong to bearer");
        }
        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(transactionService.getTransactionsByAccountIdAndDateRange(id, startDate, endDate));
        }
        if (transactionType != null) {
            return ResponseEntity.ok(transactionService.getTransactionsByAccountIdAndTransactionType(id, transactionType));
        }
        return ResponseEntity.ok(transactionService.getTransactionByAccountId(id, page, size));
    }

    @GetMapping("{id}/transactions")
    public ResponseEntity<?> getMyLastFiveTransactions(@PathVariable Long id) {
        return getAllMyTransactions(id, 0, 5);
    }

}
