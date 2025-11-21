package com.learning.shared.exception;

import lombok.Getter;

/**
 * Excepción base para errores de negocio.
 * Todas las excepciones personalizadas deben heredar de esta clase.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final int httpStatus;
    private final Object details;

    public BusinessException(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.details = null;
    }

    public BusinessException(String message, String errorCode, int httpStatus, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.details = details;
    }

    public BusinessException(String message, String errorCode, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.details = null;
    }
}