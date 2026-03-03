package com.streamcore.userservice.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception for user-service. All service-specific exceptions extend this class.
 */
public class UserServiceException extends RuntimeException {

    private final HttpStatus status;

    public UserServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
