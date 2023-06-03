package com.digitalmoney.msaccounts.application.exception;

public class BadRequestException extends Exception {
    public BadRequestException(String error) {
        super(error);
    }
}
