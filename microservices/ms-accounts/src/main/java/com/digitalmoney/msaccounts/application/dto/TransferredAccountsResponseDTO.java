package com.digitalmoney.msaccounts.application.dto;


import java.time.LocalDateTime;

public record TransferredAccountsResponseDTO(
        String name,
        String last_name,
        String cvu,
        LocalDateTime last_transfer_date
){}
