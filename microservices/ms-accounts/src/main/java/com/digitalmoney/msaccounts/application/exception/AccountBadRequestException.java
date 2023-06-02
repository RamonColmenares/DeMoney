package com.digitalmoney.msaccounts.application.exception;

public class AccountBadRequestException extends Exception{
    public AccountBadRequestException(String error) {
        super(error);
    }
}
