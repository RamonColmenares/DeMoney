package com.digitalmoney.msaccounts.service;

import com.digitalmoney.msaccounts.application.dto.AccountCreationDTO;
import com.digitalmoney.msaccounts.application.dto.AccountUpdateDTO;
import com.digitalmoney.msaccounts.application.dto.UserAccountDTO;
import com.digitalmoney.msaccounts.application.exception.AccountBadRequestException;
import com.digitalmoney.msaccounts.application.exception.AccountInternalServerException;
import com.digitalmoney.msaccounts.application.exception.AccountNotFoundException;
import com.digitalmoney.msaccounts.application.exception.AccountUnauthorizedException;
import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import com.digitalmoney.msaccounts.persistency.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

@Service @AllArgsConstructor @Log4j2
public class AccountService {
    private final AccountRepository repository;
    private final ObjectMapper mapper;

    public List<Account> findAll() {
        return repository.findAll();
    }

    public Account findById(Long accountId) {
        return repository.findById(accountId).orElse(null);
    }

    public AccountCreationDTO createAccount(UserAccountDTO userDetails) throws AccountInternalServerException {
        Account accountToStore = new Account();
        accountToStore.setUserId(userDetails.user_id());
        accountToStore.setCvu(generateCVU(userDetails.dni()));
        accountToStore.setAlias(generateAlias(userDetails.dni()));

        return mapper.convertValue(repository.save(accountToStore), AccountCreationDTO.class);
    }

    public Account findAccountByUserID(String sid) throws AccountNotFoundException, AccountUnauthorizedException, AccountBadRequestException {
        Long id = validateID(sid);

        Optional<Account> accountFound = repository.findByUserId(id);

        if (!accountFound.isPresent()) {
            throw new AccountNotFoundException("the account with user id " + id + " was not found");
        }

        //TODO: this validation must be modified once we have the api gateway

        /*//check if the user who did the request is the correct one
        String emailReq = ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        log.error(emailReq);
        log.error(userFound.get().getEmail());
        if (!userFound.get().getEmail().equals(emailReq)) {
            throw new UserUnauthorizedException("you can only request your user details");
        }*/

        return accountFound.get();
    }

    public Account updateAccount(String id, AccountUpdateDTO accountUpdates) throws AccountNotFoundException, AccountUnauthorizedException, AccountBadRequestException, AccountInternalServerException {
        Account accountFound = findAccountByUserID(id);

        Optional<Account> accAlias = repository.findByAlias(accountUpdates.alias());
        if (accAlias.isPresent()) {
            throw new AccountBadRequestException("the alias is already used by another user");
        }

        if (!validateAlias(accountUpdates.alias())){
            throw new AccountBadRequestException("the alias cannot include uppercase, numbers and the correct format is example.for.alias");
        }

        accountFound.setAlias(accountUpdates.alias());

        try {
            repository.save(accountFound);
        } catch (Exception e) {
            throw new AccountInternalServerException(e.getMessage());
        }

        return accountFound;
    }

    private String generateAlias(String dni) throws AccountInternalServerException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<String>> data = mapper.readValue(new File("words.json"), Map.class);

            List<String> words = data.get("words");
            Random rand = new Random(Long.parseLong(dni));

            String word1 = words.get(rand.nextInt(words.size()));
            String word2 = words.get(rand.nextInt(words.size()));
            String word3 = words.get(rand.nextInt(words.size()));

            return word1 + "." + word2 + "." + word3;
        } catch (IOException e) {
            log.error("Error reading words file", e);
        }
        throw new AccountInternalServerException("Could not generate alias. Try again later.");
    }

    private String generateCVU(String DNI) {
        CVUService cvuService = new CVUService(Long.parseLong(DNI));
        return cvuService.generateCVU();
    }

    public Long validateID(String id) throws AccountBadRequestException {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new AccountBadRequestException("the id must be numeric");
        }
    }

    public boolean validateAlias(String alias) {
        // regex for a correct alias with the format "word.one.two"
        String regex = "^[a-z]+\\.[a-z]+\\.[a-z]+$";

        // compile the regex with an object of Pattern type la expresión regular en un objeto de tipo Pattern
        Pattern pattern = Pattern.compile(regex);

        // verifies if the alias match with the expected format
        return pattern.matcher(alias).matches();
    }
}
