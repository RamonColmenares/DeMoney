package com.digitalmoney.msaccounts.service;

import com.digitalmoney.msaccounts.application.dto.CardDTO;
import com.digitalmoney.msaccounts.application.exception.AlreadyExistsException;
import com.digitalmoney.msaccounts.application.exception.NotFoundException;
import com.digitalmoney.msaccounts.persistency.entity.Account;
import com.digitalmoney.msaccounts.persistency.entity.Card;
import com.digitalmoney.msaccounts.persistency.repository.CardRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service @AllArgsConstructor @Log4j2
public class CardService implements ICardService {

    private final CardRepository cardRepository;
    private final AccountService accountService;

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
