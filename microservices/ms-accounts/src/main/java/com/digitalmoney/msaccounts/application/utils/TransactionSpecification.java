package com.digitalmoney.msaccounts.application.utils;

import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransactionSpecification {

    public static Specification<Transaction> findByAmountRange(Integer min, Integer max) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("amount"), min, max);
    }

    public static Specification<Transaction> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("transactionDate"), start, end);
    }

    public static Specification<Transaction> findByTransactionType(Transaction.TransactionType type) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("transactionType"), type);
    }

    public static Specification<Transaction> findByAccountId(Long accountId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("account").get("id"), accountId);
    }

    public static Specification<Transaction> orderByTransactionDateDesc() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("transactionDate")));
            return criteriaBuilder.and();
        };
    }

}
