package com.digitalmoney.msaccounts.application.exception;

public class UnauthorizedException extends Exception{
    public UnauthorizedException(String error) {
        super(error);
    }
}
