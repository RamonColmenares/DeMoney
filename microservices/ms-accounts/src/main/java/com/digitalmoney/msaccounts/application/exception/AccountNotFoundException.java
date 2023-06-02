package com.digitalmoney.msaccounts.application.exception;

public class AccountNotFoundException extends Exception{
    public AccountNotFoundException(String error) {
        super(error);
    }
}
