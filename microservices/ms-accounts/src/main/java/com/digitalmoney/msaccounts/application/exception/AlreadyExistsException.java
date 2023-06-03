package com.digitalmoney.msaccounts.application.exception;

public class AlreadyExistsException extends Exception {
    public AlreadyExistsException(String error) {
        super(error);
    }
}
