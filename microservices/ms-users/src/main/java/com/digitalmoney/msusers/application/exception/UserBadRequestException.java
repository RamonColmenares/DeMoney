package com.digitalmoney.msusers.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserBadRequestException extends Exception {
    public UserBadRequestException(String error) {
        super(error);
    }
}
