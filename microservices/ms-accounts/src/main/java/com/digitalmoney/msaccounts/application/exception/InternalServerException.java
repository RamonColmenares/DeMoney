package com.digitalmoney.msaccounts.application.exception;

public class InternalServerException extends Exception{
    public InternalServerException(String error) {
        super(error);
    }
}
