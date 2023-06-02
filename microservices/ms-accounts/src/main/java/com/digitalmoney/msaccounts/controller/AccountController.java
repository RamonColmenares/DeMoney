package com.digitalmoney.msaccounts.controller;

import com.digitalmoney.msaccounts.application.dto.AccountUpdateDTO;
import com.digitalmoney.msaccounts.application.dto.UserAccountDTO;
import com.digitalmoney.msaccounts.application.exception.ResourceNotFoundException;
import com.digitalmoney.msaccounts.application.dto.ErrorMessageDTO;
import com.digitalmoney.msaccounts.application.exception.AlreadyExistsException;
import com.digitalmoney.msaccounts.application.exception.NotFoundException;
import com.digitalmoney.msaccounts.persistency.dto.TransactionResponseDTO;
import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.persistency.entity.Card;
import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import com.digitalmoney.msaccounts.service.AccountService;
import com.digitalmoney.msaccounts.service.CardService;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import com.digitalmoney.msaccounts.application.exception.AccountBadRequestException;
import com.digitalmoney.msaccounts.application.exception.AccountInternalServerException;
import com.digitalmoney.msaccounts.application.exception.AccountNotFoundException;
import com.digitalmoney.msaccounts.application.exception.AccountUnauthorizedException;
import com.digitalmoney.msaccounts.application.dto.CardDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
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

    @GetMapping("/{id}")
    public ResponseEntity<?> findByID(@PathVariable String id) throws AccountNotFoundException, AccountBadRequestException, AccountUnauthorizedException {
        return ResponseEntity.ok().body(service.findAccountByUserID(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody @Valid AccountUpdateDTO account) throws AccountNotFoundException, AccountBadRequestException, AccountUnauthorizedException, AccountInternalServerException {
        return ResponseEntity.ok().body(service.updateAccount(id, account));
    }
    @GetMapping("/{accountId}/cards")
    public ResponseEntity<?> getAllCardsByAccountId (@PathVariable Long accountId) {

        try {
            List<CardDTO> cards = cardService.findAllByAccountId(accountId);
            System.out.println(cards);
            if (cards.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body("There are no cards associated with the account.");
            }

            return ResponseEntity.ok(cards);

        } catch (HttpClientErrorException.BadRequest e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("bad request");

        } catch (HttpClientErrorException.NotFound e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("cards not found");

        } catch (HttpClientErrorException.Unauthorized e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");

        } catch (HttpClientErrorException.Forbidden e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("forbidden");

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("an error occurred");
        }
    }

    @PostMapping("/{accountId}/cards")
    public ResponseEntity<?> addCard(@PathVariable Long accountId, @RequestBody CardDTO cardDTO){
        Card result;
        try {
            result = cardService.addCard(accountId, cardDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.status(400).body(new ErrorMessageDTO("An Account with the specified ID could not be found"));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(409).body(new ErrorMessageDTO("A Card with the specified card number already exists"));
        } catch (ConstraintViolationException e) {
            return ResponseEntity.status(400).body(new ErrorMessageDTO("Malformed Card data"));
        }
        return ResponseEntity.ok(result);
    }

        @GetMapping("/{accountId}/cards/{cardId}")
        public ResponseEntity<?> getCardByIdAndAccountId (@PathVariable Long accountId, @PathVariable Long cardId){

            CardDTO card = null;

            try {

                card = cardService.findByIdAndAccountId(cardId, accountId);
                return ResponseEntity.ok(card);

            } /*catch (HttpClientErrorException.BadRequest e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("bad request");

        } catch (HttpClientErrorException.NotFound e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("card not found");

        } catch (HttpClientErrorException.Unauthorized e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");

        } catch (HttpClientErrorException.Forbidden e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("forbidden");

        } */ catch (Exception e) {

                if (card == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("card not found");
                }

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("an error occurred");
            }
        }

    @DeleteMapping("/{accountId}/cards/{cardId}")
    public ResponseEntity<String> deleteCardByIdAndAccountId(@PathVariable Long accountId, @PathVariable Long cardId) {
        try {
            cardService.deleteCardByIdAndAccountId(accountId, cardId);
            return ResponseEntity.ok("Card deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
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
