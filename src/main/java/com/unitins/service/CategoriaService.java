package com.unitins.service;

import com.unitins.model.Categoria;

import java.util.Optional;

public interface CategoriaService {

    Categoria criarCategoria(Categoria categoria);

    Iterable<Categoria> buscarTodos();

    Optional<Categoria> buscarPorId(Long id);

    Categoria salvarCategoria(Categoria categoria); // Método para salvar/criar

    void deletarCategoria(Long id);

    Categoria atualizarCategoria(Long id, Categoria categoria);

    // Você pode adicionar outros métodos específicos de negócio aqui
}
