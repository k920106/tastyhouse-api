package com.tastyhouse.external.exception;

public class ExternalApiException extends RuntimeException {

    private final ExternalApiErrorCode errorCode;

    public ExternalApiException(ExternalApiErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public ExternalApiException(ExternalApiErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ExternalApiException(ExternalApiErrorCode errorCode, Throwable cause) {
        super(errorCode.getDefaultMessage(), cause);
        this.errorCode = errorCode;
    }

    public ExternalApiErrorCode getErrorCode() {
        return errorCode;
    }
}
