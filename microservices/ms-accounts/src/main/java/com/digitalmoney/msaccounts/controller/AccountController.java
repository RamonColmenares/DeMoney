package com.digitalmoney.msaccounts.controller;

import com.digitalmoney.msaccounts.application.dto.CardDTO;
import com.digitalmoney.msaccounts.application.dto.UserAccountDTO;
import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.persistency.entity.Card;
import com.digitalmoney.msaccounts.service.AccountService;
import com.digitalmoney.msaccounts.service.CardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

//@AllArgsConstructor
@RestController
@RequestMapping("/accounts")
public class AccountController {

    public AccountController(AccountService service, CardService cardService) {
        this.service = service;
        this.cardService = cardService;
    }
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

    @GetMapping("/{accountId}/cards/{cardId}")
    public ResponseEntity<?> getCardByIdAndAccountId(@PathVariable Long accountId, @PathVariable Long cardId) {

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

        } */catch (Exception e) {

            if (card == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("card not found");
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("an error occurred");
        }
    }

}
