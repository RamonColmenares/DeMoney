package com.digitalmoney.msaccounts.persistency.repository;

import com.digitalmoney.msaccounts.persistency.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    //SELECT * FROM cards c WHERE account_id = :accountId"
    List<Card> findAllByAccountId (Long accountId);

    //SELECT * FROM cards c WHERE account_id = :accountId AND id = :cardId;
    Card findByIdAndAccountId(Long cardId, Long accountId);

    Optional<Card> findByCardNumber(String cardNumber);
}
