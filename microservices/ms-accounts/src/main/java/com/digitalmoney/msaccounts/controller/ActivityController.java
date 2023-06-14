package com.digitalmoney.msaccounts.controller;

import com.digitalmoney.msaccounts.application.dto.TransactionActivityDTO;
import com.digitalmoney.msaccounts.persistency.entity.Transaction;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ActivityController {
    public TransactionActivityDTO getTransactionActivity(@PathVariable("accountId") Long accountId,
                                                         @PathVariable("transactionId") Integer transactionId)
    // Llamada a servicios o repositorios para obtener los datos necesarios
    Transaction transaction = transactionService.getTransaction(accountId, transactionId);

    // Paso 2: Crear objeto TransactionActivityDTO y asignar valores a los campos
    BigDecimal transactionAmount = transaction.getTransactionAmount();
    LocalDateTime transactionDate = transaction.getTransactionDate();
    Integer id = transaction.getId();
    Transaction.TransactionType transactionType = transaction.getTransactionType();

    TransactionActivityDTO activityDTO = new TransactionActivityDTO(transactionAmount, transactionDate, id, transactionType);

    // Paso 3: Devolver el objeto TransactionActivityDTO como resultado de la solicitud
        return activityDTO;

}
