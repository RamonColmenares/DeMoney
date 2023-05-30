package com.digitalmoney.msaccounts.persistency.repository;

import com.digitalmoney.msaccounts.persistency.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
//    @Query("FROM Card c WHERE c.cardNumber = ?1")
    Optional<Card> findByCardNumber(String cardNumber);
}
