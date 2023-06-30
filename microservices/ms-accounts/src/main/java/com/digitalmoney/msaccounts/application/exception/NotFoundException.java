package com.digitalmoney.msaccounts.application.exception;

public class NotFoundException extends Exception{
    public NotFoundException(String error) {
        super(error);
    }
}
