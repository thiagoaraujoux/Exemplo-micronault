package com.unitins.service.exception;

public class AcessoNaoAutorizadoException extends RuntimeException {
    public AcessoNaoAutorizadoException(String message) {
        super(message);
    }
}