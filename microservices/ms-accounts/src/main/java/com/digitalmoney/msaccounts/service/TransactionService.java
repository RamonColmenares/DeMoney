package com.digitalmoney.msaccounts.service;

import com.digitalmoney.msaccounts.application.dto.*;
import com.digitalmoney.msaccounts.application.exception.BadRequestException;
import com.digitalmoney.msaccounts.application.exception.InternalServerException;
import com.digitalmoney.msaccounts.application.utils.TransactionSpecification;
import com.digitalmoney.msaccounts.application.exception.NotFoundException;
import com.digitalmoney.msaccounts.persistency.dto.TransferenceRequest;
import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import com.digitalmoney.msaccounts.persistency.repository.AccountRepository;
import com.digitalmoney.msaccounts.persistency.repository.TransactionRepository;
import com.digitalmoney.msaccounts.service.feign.UserFeignService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service @AllArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;
    private final UserFeignService userFeignService;
    private final AccountRepository accountRepository;
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
        transaction.setDestinationCvu(account.getCvu());
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

    public List<TransferredAccountsResponseDTO> getLastFiveTransferredAccounts (Long accountId) throws Exception {

        List <TransferredAccountsResponseDTO> result = new ArrayList<>();

        Optional<Account> account = accountRepository.findById(accountId);
        try {

            List<Transaction> resultQuery = repository.findByAccountIdAndDestinationCvuNotAndTransactionTypeNotOrderByTransactionDateDesc(accountId, account.get().getCvu(), Transaction.TransactionType.income);
            Set<String> addedCvus = new HashSet<>();

            System.out.println(resultQuery.toString());

            for (Transaction accountResponse: resultQuery){
                System.out.println(accountResponse.toString());
                if (addedCvus.contains(accountResponse.getDestinationCvu())){
                    continue;
                } else {
                    addedCvus.add(accountResponse.getDestinationCvu());
                }
                Optional<Account> account1 = accountRepository.findByCvu(accountResponse.getDestinationCvu());
                System.out.println(account1.toString());

                System.out.println("get user");
                if (account1.isPresent()){
                    UserDTO userDTO = userFeignService.findUserById(account1.get().getUserId().toString()).getBody();
                    result.add(new TransferredAccountsResponseDTO(userDTO.name(), userDTO.last_name(), accountResponse.getDestinationCvu(), accountResponse.getTransactionDate()));
                } else {
                    result.add(new TransferredAccountsResponseDTO("","", accountResponse.getDestinationCvu(), accountResponse.getTransactionDate()));
                }

                if (addedCvus.size() > 4){
                    break;
                }
            }
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }

        return result;

    }

}
