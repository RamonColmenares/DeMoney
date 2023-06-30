package com.digitalmoney.msaccounts.service;

import com.digitalmoney.msaccounts.application.dto.CardDTO;
import com.digitalmoney.msaccounts.application.dto.CardResponseDTO;
import com.digitalmoney.msaccounts.application.exception.BadRequestException;
import com.digitalmoney.msaccounts.application.exception.InternalServerException;
import com.digitalmoney.msaccounts.application.exception.NotFoundException;
import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.persistency.entity.Card;
import com.digitalmoney.msaccounts.persistency.repository.CardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
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
    private final CVVService cvvService;

    ObjectMapper mapper;

    public List<Card> findAll(){
        return cardRepository.findAll();
    }

    public Card findByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber).orElse(null);
    }

    public Card createCard(CardDTO cardDTO) throws Exception {
        return new Card(null, cardDTO.cardNumber(), cardDTO.cardHolder(), cardDTO.expirationDate(), cvvService.cryptCVV(cardDTO.cvv(), cardDTO.cardNumber()), null);
    }

    public Card addCard(Long accountId, CardDTO cardDTO) throws NotFoundException, InternalServerException, BadRequestException {
        Account account = accountService.findById(accountId);
        if (account == null) {
            throw new NotFoundException("No account was found with the specified ID");
        }
        if (findByCardNumber(cardDTO.cardNumber()) != null) {
            throw new BadRequestException("A card with the specified card number already exists");
        }

        Card card;
        try {
            card = createCard(cardDTO);
            card.setAccount(account);
            cardRepository.save(card);
        }catch (ValidationException e){
            throw new BadRequestException("Card number is invalid");
        }catch (Exception e){
            log.error(e.getMessage());
            throw new InternalServerException("there was a problem adding your card, please try again later");
        }

        return card;
    }

    public List<CardResponseDTO> findAllByAccountId(Long accountId) {
        List<Card> cards = cardRepository.findAllByAccountId(accountId);
        List<CardResponseDTO> cardDTOS = new ArrayList<>();
        for (Card card: cards) {
            CardResponseDTO cardDTO = new CardResponseDTO(card.getCardNumber(), card.getCardHolder(), card.getExpirationDate());
            cardDTOS.add(cardDTO);
        }
        return cardDTOS;
    }

    public CardResponseDTO findByIdAndAccountId(Long cardId, Long accountId) throws NotFoundException {
        Card card = cardRepository.findByIdAndAccountId(cardId, accountId);
        if(card != null) {
            return new CardResponseDTO(card.getCardNumber(), card.getCardHolder(), card.getExpirationDate());
        } else {
            throw new NotFoundException("Card not found");
        }
    }

    public void deleteCardByIdAndAccountId(Long accountId, Long cardId) throws NotFoundException {
        Optional<CardResponseDTO> optionalCardDTO = Optional.ofNullable(findByIdAndAccountId(cardId, accountId));
        if (optionalCardDTO.isPresent()) {
            cardRepository.deleteById(cardId);
        } else {
            throw new NotFoundException("Card not found");
        }
    }
}
