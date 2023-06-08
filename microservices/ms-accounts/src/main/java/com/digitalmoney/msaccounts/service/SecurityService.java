package com.digitalmoney.msaccounts.service;

import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.persistency.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service @AllArgsConstructor
public class SecurityService {
    private final AccountRepository repository;
    public boolean isMyAccount(Long idAccount) {
        if (idAccount == null) {
            throw new IllegalArgumentException("Account ID cannot be null");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new SecurityException("Could not get authentication object from security context");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof String)) {
            throw new SecurityException("Principal object is not of type Long");
        }

        Long id;
        try {
             id = Long.valueOf((String) principal);
        } catch (Exception e) {
            throw new SecurityException("Bearer is unauthorized");
        }
        Optional<Account> accountOptional = repository.findByUserId(id);

        if (accountOptional.isEmpty()) {
            throw new NoSuchElementException("Could not find user with ID " + id);
        }

        Account user = accountOptional.get();
        return idAccount.equals(user.getId());
    }
}
