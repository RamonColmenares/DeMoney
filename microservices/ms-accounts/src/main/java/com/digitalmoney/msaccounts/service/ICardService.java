package com.digitalmoney.msaccounts.service;

import com.digitalmoney.msaccounts.application.dto.CardDTO;

import java.util.List;

public interface ICardService {

    List<CardDTO> findAllByAccountId (Long accountId);

    CardDTO findByIdAndAccountId(Long cardId, Long accountId);

    void deleteCardByIdAndAccountId(Long accountId, Long cardId) throws Exception;

}
