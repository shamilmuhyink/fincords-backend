package com.fincords.exception;

public class InvalidMobileNumberException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private int errorCode;
    private String errorMessage;

    public InvalidMobileNumberException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
