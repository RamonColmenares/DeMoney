package com.digitalmoney.msaccounts.application.exception;

public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(String error) {
        super(error);
    }
}
