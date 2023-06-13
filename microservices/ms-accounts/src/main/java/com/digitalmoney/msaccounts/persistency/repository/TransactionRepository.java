package com.digitalmoney.msaccounts.persistency.repository;

import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT new com.digitalmoney.msaccounts.application.dto.TransactionActivityDTO(t.amount, t.transactionDate, t.id, t.transactionType) from Transaction t where t.account.id = :idAccount order by t.transactionDate desc")
    Page<Transaction> getTransactionByAccountId(@RequestParam Long idAccount, Pageable pageable);

    @Query("SELECT t from Transaction t where t.account.id = :idAccount AND t.transactionDate >= :startDate AND t.transactionDate <= :endDate order by t.transactionDate desc")
    List<Transaction> getTransactionByAccountIdAndDateRange(@RequestParam Long idAccount, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :idAccount AND t.transactionType = :transactionType ORDER BY t.transactionDate DESC")
    List<Transaction> getTransactionsByAccountIdAndTransactionType(@RequestParam Long idAccount, Transaction.TransactionType transactionType);
}
