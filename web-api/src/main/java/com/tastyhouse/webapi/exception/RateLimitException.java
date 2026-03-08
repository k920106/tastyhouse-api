package com.tastyhouse.webapi.exception;

import com.tastyhouse.core.exception.ErrorCode;

public class RateLimitException extends RuntimeException {

    public RateLimitException(String message) {
        super(message);
    }

    public RateLimitException() {
        super(ErrorCode.RATE_LIMIT_EXCEEDED.getDefaultMessage());
    }
}
