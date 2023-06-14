package com.digitalmoney.msaccounts.persistency.repository;

import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAll(Specification<Transaction> specification, Pageable pageable);

}
