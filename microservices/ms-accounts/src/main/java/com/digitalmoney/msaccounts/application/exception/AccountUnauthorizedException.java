package com.digitalmoney.msaccounts.application.exception;

public class AccountUnauthorizedException extends Exception{
    public AccountUnauthorizedException(String error) {
        super(error);
    }
}
