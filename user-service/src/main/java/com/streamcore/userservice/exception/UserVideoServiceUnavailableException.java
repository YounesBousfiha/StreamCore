package com.streamcore.userservice.exception;

import org.springframework.http.HttpStatus;

public class UserVideoServiceUnavailableException extends UserServiceException {

    public UserVideoServiceUnavailableException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
