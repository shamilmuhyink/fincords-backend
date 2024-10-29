package com.fincords.exception;

public class TwilioException extends RuntimeException {
    public TwilioException(String message) {
        super(message);
    }
}
