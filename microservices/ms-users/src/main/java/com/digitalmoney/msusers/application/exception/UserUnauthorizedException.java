package com.digitalmoney.msusers.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserUnauthorizedException extends Exception{
    public UserUnauthorizedException(String error) {
        super(error);
    }
}
