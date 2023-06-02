package com.digitalmoney.msaccounts.service;

import com.digitalmoney.msaccounts.application.dto.CardDTO;
import com.digitalmoney.msaccounts.persistency.entity.Card;
import com.digitalmoney.msaccounts.persistency.repository.AccountRepository;
import com.digitalmoney.msaccounts.persistency.repository.CardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service  @Log4j2 //@AllArgsConstructor
public class CardService {

    public CardService (CardRepository cardRepository, AccountRepository accountRepository) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
    }

    private final CardRepository cardRepository;

    private final AccountRepository accountRepository;

    ObjectMapper mapper;

    public List<Card> findAll(){
        return cardRepository.findAll();
    }

    public List<CardDTO> findAllByAccountId(Long accountId) {
        List<Card> cards = cardRepository.findAllByAccountId(accountId);
        List<CardDTO> cardDTOS = new ArrayList<>();
        for (Card card: cards) {
            CardDTO cardDTO = new CardDTO(card.getCardNumber(), card.getCardHolder(), card.getExpirationDate(), card.getCvv());
            cardDTOS.add(cardDTO);
        }
        return cardDTOS;

    }

    public CardDTO findByIdAndAccountId(Long cardId, Long accountId) {

        Card card = cardRepository.findByIdAndAccountId(cardId, accountId);
        return new CardDTO(card.getCardNumber(), card.getCardHolder(), card.getExpirationDate(), card.getCvv());

    }



}
