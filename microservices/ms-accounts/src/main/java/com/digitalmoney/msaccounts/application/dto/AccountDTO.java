package com.digitalmoney.msaccounts.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class AccountDTO {
    private Long id;
    private String cvu;
    private String alias;
}
