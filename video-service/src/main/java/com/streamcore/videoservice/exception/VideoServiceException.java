package com.streamcore.videoservice.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception for video-service. All service-specific exceptions extend this class.
 */
public class VideoServiceException extends RuntimeException {

    private final HttpStatus status;

    public VideoServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
