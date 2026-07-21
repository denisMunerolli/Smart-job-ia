package com.smartjobai.core.exception;

/**
 * Erro de regra de negócio (ex.: email duplicado, acesso a recurso de outro usuário).
 * Mapeado para HTTP 400 pelo GlobalExceptionHandler.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
