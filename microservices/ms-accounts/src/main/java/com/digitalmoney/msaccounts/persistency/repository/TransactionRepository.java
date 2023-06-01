package com.digitalmoney.msaccounts.persistency.repository;

import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Long, Transaction> {
    @Query("SELECT t from Transaction t where t.account.id = :idAccount order by t.transactionDate desc")
    Page<Transaction> getTransactionByAccountId(Long idAccount, Pageable pageable);
}
