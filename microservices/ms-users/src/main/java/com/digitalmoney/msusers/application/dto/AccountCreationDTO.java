package com.digitalmoney.msusers.application.dto;

import lombok.Data;

@Data
public class AccountCreationDTO {
    private Long id;
    private Long user_id;
    private String cvu;
    private String alias;
};
