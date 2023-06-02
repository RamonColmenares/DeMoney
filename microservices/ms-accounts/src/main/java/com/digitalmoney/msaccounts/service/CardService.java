package com.digitalmoney.msaccounts.service;

import com.digitalmoney.msaccounts.application.dto.CardDTO;
import com.digitalmoney.msaccounts.application.exception.ResourceNotFoundException;
import com.digitalmoney.msaccounts.application.exception.AlreadyExistsException;
import com.digitalmoney.msaccounts.application.exception.NotFoundException;
import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.persistency.entity.Card;
import com.digitalmoney.msaccounts.persistency.repository.AccountRepository;
import com.digitalmoney.msaccounts.persistency.repository.CardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service  @Log4j2 @AllArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final AccountService accountService;



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

    public CardDTO findByIdAndAccountId(Long cardId, Long accountId) throws ResourceNotFoundException {

        Card card = cardRepository.findByIdAndAccountId(cardId, accountId);
        if(card != null) {
            return new CardDTO(card.getCardNumber(), card.getCardHolder(), card.getExpirationDate(), card.getCvv());
        } else {
            throw new ResourceNotFoundException("Card not found");
        }

    }

    public void deleteCardByIdAndAccountId(Long accountId, Long cardId) throws Exception {
        Optional<CardDTO> optionalCardDTO = Optional.ofNullable(findByIdAndAccountId(accountId, cardId));
        if (optionalCardDTO.isPresent()) {
            cardRepository.deleteById(cardId);
        } else {
            throw new ResourceNotFoundException("Card not found");
        }
    }

    public Card findByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber).orElse(null);
    }

    public Card addCard(Long accountId, CardDTO cardDTO) throws NotFoundException, AlreadyExistsException {
        Account account = accountService.findById(accountId);
        if (account == null) {
            throw new NotFoundException("No account was found with the specified ID");
        }
        if (findByCardNumber(cardDTO.cardNumber()) != null) {
            throw new AlreadyExistsException("A card with the specified card number already exists");
        }

        Card card = createCard(cardDTO);
        card.setAccount(account);

        return cardRepository.save(card);
    }

    public Card createCard(CardDTO cardDTO){
        return new Card(null, cardDTO.cardNumber(), cardDTO.cardHolder(), cardDTO.expirationDate(), cardDTO.cvv(), null);
    }
}
