package com.digitalmoney.msaccounts.application.exception;

public class AccountInternalServerException extends Exception{
    public AccountInternalServerException(String error) {
        super(error);
    }
}
