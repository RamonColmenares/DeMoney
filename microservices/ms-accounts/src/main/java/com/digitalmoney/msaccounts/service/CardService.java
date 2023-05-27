package com.digitalmoney.msaccounts.service;

import com.digitalmoney.msaccounts.application.dto.CardDTO;
import com.digitalmoney.msaccounts.application.exception.ResourceNotFoundException;
import com.digitalmoney.msaccounts.persistency.entity.Card;
import com.digitalmoney.msaccounts.persistency.repository.CardRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service @AllArgsConstructor @Log4j2
public class CardService implements ICardService {

    private final CardRepository cardRepository;

    public List<Card> findAll(){
        return cardRepository.findAll();
    }

    @Override
    public List<CardDTO> findAllByAccountId(Long accountId) {
        return null;
    }

    @Override
    public CardDTO findByIdAndAccountId(Long cardId, Long accountId) {
        return null;
    }

    @Override
    public void deleteCardByIdAndAccountId(Long accountId, Long cardId) throws Exception {
        Optional<CardDTO> optionalCardDTO = Optional.ofNullable(findByIdAndAccountId(accountId, cardId));

        if (optionalCardDTO.isPresent()) {
            cardRepository.deleteById(cardId);
        } else {
            throw new ResourceNotFoundException("Card not found");
        }
    }

}
