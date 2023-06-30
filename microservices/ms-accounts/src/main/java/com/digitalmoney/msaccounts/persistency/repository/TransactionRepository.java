package com.digitalmoney.msaccounts.persistency.repository;

import com.digitalmoney.msaccounts.application.dto.TransactionResponseDTO;
import com.digitalmoney.msaccounts.application.dto.TransferredAccountsResponseDTO;
import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAll(Specification<Transaction> specification, Pageable pageable);

    List<Transaction> findByAccountIdAndDestinationNotAndTransactionTypeNotOrderByTransactionDateDesc (@Param("accountId") Long accountId, @Param("cvu") String cvu, @Param("transactionType")Transaction.TransactionType transactionType);

}
