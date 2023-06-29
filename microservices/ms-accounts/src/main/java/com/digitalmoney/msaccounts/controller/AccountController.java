package com.digitalmoney.msaccounts.controller;

import com.digitalmoney.msaccounts.application.dto.AccountUpdateDTO;
import com.digitalmoney.msaccounts.application.dto.CardDTO;
import com.digitalmoney.msaccounts.application.dto.CardResponseDTO;
import com.digitalmoney.msaccounts.application.dto.UserAccountDTO;
import com.digitalmoney.msaccounts.application.exception.*;
import com.digitalmoney.msaccounts.application.dto.*;
import com.digitalmoney.msaccounts.persistency.dto.TransferenceRequest;
import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.persistency.entity.Card;
import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import com.digitalmoney.msaccounts.service.AccountService;
import com.digitalmoney.msaccounts.service.CardService;
import com.digitalmoney.msaccounts.service.SecurityService;
import com.digitalmoney.msaccounts.service.TransactionService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Response;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/user/{uid}")
    public ResponseEntity<?> findByUserID(@PathVariable String uid) throws NotFoundException, BadRequestException, UnauthorizedException {
        return ResponseEntity.ok().body(service.findAccountByUserID(uid));
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
    public ResponseEntity<?> getAllMyTransactions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(required = false) Integer minAmount,
            @RequestParam(required = false) Integer maxAmount,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String transactionType) throws BadRequestException {

        if(!securityService.isMyAccount(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account doesnt belong to bearer");
        }
        return ResponseEntity.ok(transactionService.getTransactionsByAccountId(id, limit, minAmount, maxAmount, startDate, endDate, transactionType));
    }

    @GetMapping("{id}/activity/{transactionID}")
    public ResponseEntity<?> getDetailOfTransaction(@PathVariable Long id, @PathVariable Long transactionID) throws NotFoundException, BadRequestException {
        if(!securityService.isMyAccount(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account doesnt belong to bearer");
        }
        return ResponseEntity.ok(transactionService.getTransactionDetail(id, transactionID));
    }

    @GetMapping("{id}/transferences")
    public ResponseEntity<?> getMyLastTenTransactions(@PathVariable Long id) throws BadRequestException {
        if(!securityService.isMyAccount(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account doesnt belong to bearer");
        }
        return ResponseEntity.ok(transactionService.getTransactionsByAccountId(id, 10, 0, 0, null, null, null)
                .stream()
                .map(e -> {
                    try {
                        return transactionService.getTransactionDetail(id, e.getTransactionId());
                    } catch (Exception ignored) {
                        // the code should never reach here, the transactions SHOULD exist.
                    }
                    return null;
                }).collect(Collectors.toList()));
    }

    @PostMapping("/{id}/deposits")
    public ResponseEntity<?> createTransactionFromCard (@RequestBody TransferenceRequest transferenceRequest, @PathVariable Long id) {
        try {
            if (securityService.isMyAccount(id)) {
                TransactionResponseDTO transaction = transactionService.createTransactionFromCard(transferenceRequest, service.findAccountByID(id.toString()));
                if (cardService.findByIdAndAccountId(transferenceRequest.cardId(), id) != null) {
                    return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account does not belong to bearer.");
            }
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");

    }

    @PostMapping("/{id}/transferences")
    public ResponseEntity<?> createTransference(@RequestBody com.digitalmoney.msaccounts.application.dto.TransferenceRequest transferenceRequest, @PathVariable Long id) {
        try {
            if (securityService.isMyAccount(id)) {
                TransferenceResponseDTO transference = transactionService.createTransference(transferenceRequest, service.findAccountByID(id.toString()));
                return ResponseEntity.status(HttpStatus.OK).body(transference);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account does not belong to bearer.");
            }
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (GoneException e) {
            return ResponseEntity.status(HttpStatus.GONE).body(e.getMessage());
        } catch (TransferenceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @GetMapping("{id}/transaction/{transactionId}/receipt")
    @ResponseBody
    public ResponseEntity<?> downloadTransactionDetailPDF(@PathVariable Long id, @PathVariable Long transactionId, HttpServletResponse response) throws IOException, NotFoundException, BadRequestException {
        if(!securityService.isMyAccount(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account doesnt belong to bearer");
        }
        PDDocument document = transactionService.downloadTransactionDetail(id, transactionId);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=transaction" + transactionId + ".pdf");

        document.save(response.getOutputStream());
        document.close();
        return ResponseEntity.ok().body("Downloading receipt");
    }

}
