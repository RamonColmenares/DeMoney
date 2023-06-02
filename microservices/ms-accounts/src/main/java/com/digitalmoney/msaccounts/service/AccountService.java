package com.digitalmoney.msaccounts.service;

import com.digitalmoney.msaccounts.application.dto.AccountCreationDTO;
import com.digitalmoney.msaccounts.application.dto.UserAccountDTO;
import com.digitalmoney.msaccounts.application.exception.AccountCreationException;
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
import java.util.Random;

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

    public AccountCreationDTO createAccount(UserAccountDTO userDetails) throws AccountCreationException {
        Account accountToStore = new Account();
        accountToStore.setUserId(userDetails.user_id());
        accountToStore.setCvu(generateCVU(userDetails.dni()));
        accountToStore.setAlias(generateAlias(userDetails.dni()));

        return mapper.convertValue(repository.save(accountToStore), AccountCreationDTO.class);
    }

    private String generateAlias(String dni) throws AccountCreationException {
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
        throw new AccountCreationException("Could not generate alias. Try again later.");
    }

    private String generateCVU(String DNI) {
        CVUService cvuService = new CVUService(Long.parseLong(DNI));
        return cvuService.generateCVU();
    }
}
