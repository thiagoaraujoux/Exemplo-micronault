package com.unitins.service;

import com.unitins.model.Usuario;

import java.util.Optional;

// Interface que define as operações de negócio para Usuario
public interface UsuarioService {

    Usuario criarUsuario(Usuario usuario);

    Iterable<Usuario> buscarTodos();

    Optional<Usuario> buscarPorId(Long id);

    Usuario salvarUsuario(Usuario usuario); // Método para salvar/criar

    void deletarUsuario(Long id);

    Usuario atualizarUsuario(Long id, Usuario usuario);

    // Você pode adicionar outros métodos específicos de negócio aqui, como buscar por email
    Optional<Usuario> buscarPorEmail(String email);
}
