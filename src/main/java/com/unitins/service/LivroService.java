package com.unitins.service;

import java.util.Optional;

import com.unitins.model.Livro;

public interface LivroService {
    Livro salvarLivro(Livro livro);

    Optional<Livro> buscarPorId(Long id);

    Iterable<Livro> buscarTodos();

    Livro atualizarLivro(Long id, Livro livro) throws LivroNaoEncontradoException;

    void deletarLivro(Long id) throws LivroNaoEncontradoException;
    // Outros métodos de negócio específicos para livros
}