package com.streamcore.userservice.exception;

import org.springframework.http.HttpStatus;

public class UserBadRequestException extends UserServiceException {

    public UserBadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
