package com.fincords.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public AuthException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }
}
