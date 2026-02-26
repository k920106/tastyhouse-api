package com.tastyhouse.core.exception;

public class AccessDeniedException extends BusinessException {

    public AccessDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
