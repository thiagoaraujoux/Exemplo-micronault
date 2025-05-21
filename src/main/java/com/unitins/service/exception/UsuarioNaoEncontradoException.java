package com.unitins.service.exception; // <--- THIS PACKAGE DECLARATION IS CRUCIAL

// Exceção personalizada para indicar que um usuário não foi encontrado
public class UsuarioNaoEncontradoException extends RuntimeException {

    // Constructor that accepts a message
    public UsuarioNaoEncontradoException(String message) {
        super(message);
    }

}
