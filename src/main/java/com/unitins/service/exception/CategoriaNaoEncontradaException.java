package com.unitins.service.exception;

public class CategoriaNaoEncontradaException extends RuntimeException {

    public CategoriaNaoEncontradaException(String message) {
        super(message);
    }

    public CategoriaNaoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
}
