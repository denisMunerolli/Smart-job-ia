package com.smartjobai.core.exception;

/**
 * Recurso não encontrado (ex.: formação/experiência/idioma com id inexistente).
 * Mapeado para HTTP 404 pelo GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
