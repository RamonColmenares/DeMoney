package com.digitalmoney.msusers.persistency.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Account {
    private Long id;
    private Long user_id;
    private String cvu;
    private String alias;
}
