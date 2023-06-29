package com.digitalmoney.msaccounts.service;

import com.digitalmoney.msaccounts.application.dto.TransactionActivityDTO;
import com.digitalmoney.msaccounts.application.dto.TransferenceResponseDTO;
import com.digitalmoney.msaccounts.application.exception.BadRequestException;
import com.digitalmoney.msaccounts.application.dto.TransactionResponseDTO;
import com.digitalmoney.msaccounts.application.exception.GoneException;
import com.digitalmoney.msaccounts.application.exception.TransferenceException;
import com.digitalmoney.msaccounts.application.utils.TransactionSpecification;
import com.digitalmoney.msaccounts.application.dto.TransactionDetailDTO;
import com.digitalmoney.msaccounts.application.exception.NotFoundException;
import com.digitalmoney.msaccounts.persistency.dto.TransferenceRequest;
import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import com.digitalmoney.msaccounts.persistency.repository.AccountRepository;
import com.digitalmoney.msaccounts.persistency.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service @AllArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final ObjectMapper mapper;

    public List<Transaction> findAll(){
        return repository.findAll();
    }

    public TransactionResponseDTO createTransactionFromCard(TransferenceRequest transferenceRequest, Account account) throws BadRequestException {

        if (transferenceRequest.transactionAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero.");
        }

        LocalDateTime date = LocalDateTime.now();

        Transaction transaction = new Transaction();

        transaction.setAmount(transferenceRequest.transactionAmount());
        transaction.setTransactionDate(date);
        transaction.setDestination(account.getCvu());
        transaction.setOriginCvu(transferenceRequest.originCvu());
        transaction.setAccount(account);
        transaction.setTransactionDescription("Deposit from credit/debit card.");
        transaction.setTransactionType(Transaction.TransactionType.income);

        repository.save(transaction);

        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO(
                account.getId(),
                transferenceRequest.transactionAmount(),
                date,
                "Deposit from credit/debit card.",
                account.getCvu(),
                transaction.getId(),
                transferenceRequest.originCvu(),
                Transaction.TransactionType.income
        );

        return transactionResponseDTO;

    }

    public List<TransactionActivityDTO> getTransactionsByAccountId(Long idAccount, int limit, Integer minAmount, Integer maxAmount, LocalDate startDate, LocalDate endDate, String transactionTypeString) throws BadRequestException {
        Specification<Transaction> specification = Specification.where(null);

        if (idAccount == null) {
            throw new BadRequestException("the account id is necessary to search transactions");
        }
        specification = specification.and(TransactionSpecification.findByAccountId(idAccount));

        if ((minAmount != null && minAmount >= 0) && (maxAmount != null && maxAmount > 0)) {
            specification = specification.and(TransactionSpecification.findByAmountRange(minAmount, maxAmount));
        }

        if (startDate != null && endDate != null) {
            specification = specification.and(TransactionSpecification.findByDateRange(startDate.atTime(LocalTime.MIDNIGHT), endDate.atTime(LocalTime.MAX)));
        }

        if (transactionTypeString != null ) {
            Transaction.TransactionType type;
            try {
                type = Transaction.TransactionType.valueOf(transactionTypeString);
            }catch (IllegalArgumentException e){
                throw new BadRequestException("transaction type is invalid");
            }
            specification = specification.and(TransactionSpecification.findByTransactionType(type));
        }

        specification = specification.and(TransactionSpecification.orderByTransactionDateDesc());

        Pageable pageable = PageRequest.of(0, limit);
        List<Transaction> transactions = repository.findAll(specification, pageable);


        return transactions.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private TransactionActivityDTO convertToDto(Transaction transaction) {
        // Convert Transaction to TransactionDTO
        return new TransactionActivityDTO(
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getId(),
                transaction.getTransactionType()
        );
    }

    public TransactionDetailDTO getTransactionDetail(Long id, Long transactionID) throws NotFoundException, BadRequestException {
        Transaction transaction = repository.findById(transactionID).orElse(null);
        if (transaction == null) {
            throw new NotFoundException("Transaction doesnt exist.");
        }
        if (!Objects.equals(transaction.getAccount().getId(), id)) {
            throw new BadRequestException("Transaction doesnt belong to the account.");
        }

        return mapper.convertValue(transaction, TransactionDetailDTO.class);
    }

    @Transactional
    public TransferenceResponseDTO createTransference(com.digitalmoney.msaccounts.application.dto.TransferenceRequest transferenceRequest, Account account) throws BadRequestException, GoneException, NotFoundException, TransferenceException {

        if(transferenceRequest.transactionAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero.");
        }

        if(BigDecimal.valueOf(account.getBalance()).compareTo(transferenceRequest.transactionAmount()) < 0) {
            throw new GoneException("Insufficient funds.");
        }

        if(transferenceRequest.destination().contains(".")) {
            if(!accountService.validateAlias(transferenceRequest.destination()) || transferenceRequest.destination().length() > 22) {
                throw new BadRequestException("Invalid alias.");
            }
        } else {
            if(!transferenceRequest.destination().matches("\\d+") || transferenceRequest.destination().length() != 22) {
                throw new BadRequestException("Invalid CVU o CBU.");
            }
        }

        Optional<Account> destinationAccount;
        if(!transferenceRequest.destination().contains(".")) {
            destinationAccount = accountRepository.findByCvu(transferenceRequest.destination());
        } else {
            destinationAccount = accountRepository.findByAlias(transferenceRequest.destination());
        }

        if (destinationAccount.isPresent()) {
            if (account.getAlias().equals(destinationAccount.get().getAlias())) {
                throw new BadRequestException("The destination account must be different from the source account.");
            }
        }

        Transaction transaction = new Transaction();
        LocalDateTime date = LocalDateTime.now();
        transaction.setDestination(destinationAccount.isPresent() ? destinationAccount.get().getCvu() : transferenceRequest.destination());
        transaction.setAmount(transferenceRequest.transactionAmount());
        transaction.setTransactionDate(date);
        transaction.setOriginCvu(account.getCvu());
        transaction.setAccount(account);
        transaction.setTransactionDescription("Transference to another account.");
        transaction.setTransactionType(Transaction.TransactionType.expense);

        Long transactionId;

        try {
            repository.save(transaction);
            transactionId = transaction.getId();
            if (destinationAccount.isPresent()) {
                Transaction transactionIncome = transaction.cloneAsIncome();
                transactionIncome.setAccount(destinationAccount.get());
                repository.save(transactionIncome);
            }
        } catch (Exception e) {
            throw new TransferenceException("Error during funds transfer");
        }

        TransferenceResponseDTO transactionResponseDTO = new TransferenceResponseDTO(
                account.getId(),
                transferenceRequest.transactionAmount(),
                date,
                "Transference to another account.",
                destinationAccount.isPresent() ? destinationAccount.get().getCvu() : transferenceRequest.destination(),
                transactionId,
                account.getCvu(),
                Transaction.TransactionType.expense
        );

        return transactionResponseDTO;

    }

}
