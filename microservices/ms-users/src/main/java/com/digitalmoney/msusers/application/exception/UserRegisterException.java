package com.digitalmoney.msusers.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UserRegisterException extends Exception{
    public UserRegisterException(String error) {
        super(error);
    }
}
