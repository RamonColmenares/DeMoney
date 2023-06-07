package com.digitalmoney.msaccounts.service;

import com.digitalmoney.msaccounts.application.dto.AccountCreationDTO;
import com.digitalmoney.msaccounts.application.dto.AccountUpdateDTO;
import com.digitalmoney.msaccounts.application.dto.UserAccountDTO;
import com.digitalmoney.msaccounts.application.exception.BadRequestException;
import com.digitalmoney.msaccounts.application.exception.InternalServerException;
import com.digitalmoney.msaccounts.application.exception.NotFoundException;
import com.digitalmoney.msaccounts.persistency.entity.Account;
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

    public AccountCreationDTO createAccount(UserAccountDTO userDetails) throws InternalServerException {
        Account accountToStore = new Account();
        accountToStore.setUserId(userDetails.user_id());
        accountToStore.setCvu(generateCVU(userDetails.dni()));
        accountToStore.setAlias(generateAlias(userDetails.dni()));
        accountToStore.setBalance(0.0);

        return mapper.convertValue(repository.save(accountToStore), AccountCreationDTO.class);
    }

    public Account findAccountByUserID(String sid) throws NotFoundException, BadRequestException {
        Long id = validateID(sid);

        Optional<Account> accountFound = repository.findByUserId(id);

        if (!accountFound.isPresent()) {
            throw new NotFoundException("the account for user id " + id + " was not found");
        }

        return accountFound.get();
    }

    public Account findAccountByID(String id) throws NotFoundException, BadRequestException {
        Long aid = validateID(id);

        Optional<Account> accountFound = repository.findById(aid);

        if (!accountFound.isPresent()) {
            throw new NotFoundException("the account with id " + aid + " was not found");
        }

        return accountFound.get();
    }

    public Account updateAccount(String id, AccountUpdateDTO accountUpdates) throws NotFoundException, BadRequestException, InternalServerException {
        Account accountFound = findAccountByID(id);

        Optional<Account> accAlias = repository.findByAlias(accountUpdates.alias());
        if (accAlias.isPresent()) {
            throw new BadRequestException("the alias is already used by another user");
        }

        if (!validateAlias(accountUpdates.alias())){
            throw new BadRequestException("the alias is not valid. The correct format is example.for.alias");
        }

        accountFound.setAlias(accountUpdates.alias());

        try {
            repository.save(accountFound);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }

        return accountFound;
    }

    private String generateAlias(String dni) throws InternalServerException {
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
        throw new InternalServerException("Could not generate alias. Try again later.");
    }

    private String generateCVU(String DNI) {
        CVUService cvuService = new CVUService(Long.parseLong(DNI));
        return cvuService.generateCVU();
    }

    public Long validateID(String id) throws BadRequestException {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException("the id must be numeric");
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
