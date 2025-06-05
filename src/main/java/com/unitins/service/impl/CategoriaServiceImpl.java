// src/main/java/com/unitins/service/impl/CategoriaServiceImpl.java
package com.unitins.service.impl;

import com.unitins.model.Categoria;
import com.unitins.repository.CategoriaRepository;
import com.unitins.service.CategoriaService;
import com.unitins.service.exception.CategoriaNaoEncontradaException;

import jakarta.inject.Singleton;
import io.micronaut.transaction.annotation.Transactional;

import java.util.Optional;

@Singleton
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional
    public Categoria criarCategoria(Categoria categoria) {
        // Lógica adicional de negócio antes de salvar, se necessário
        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<Categoria> buscarTodos() {
        return categoriaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    @Override
    @Transactional
    public Categoria salvarCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public void deletarCategoria(Long id) {
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);
        if (categoriaOptional.isPresent()) {
            categoriaRepository.delete(categoriaOptional.get());
        } else {
            throw new CategoriaNaoEncontradaException("Categoria com ID " + id + " não encontrada");
        }
    }

    @Override
    @Transactional
    public Categoria atualizarCategoria(Long id, String novoNome) {
        System.out.println("Attempting to update category with ID: " + id + " to name: " + novoNome);

        // 1. Primeiro, verifique se a categoria que se deseja atualizar existe
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);
        if (categoriaOptional.isEmpty()) {
            System.out.println("Category with ID " + id + " not found in repository for update.");
            throw new CategoriaNaoEncontradaException("Categoria com ID " + id + " não encontrada para atualização.");
        }

        // 2. Verifique se o 'novoNome' já existe para OUTRA categoria
        // Isso impede que você atualize uma categoria para um nome que já é usado por outra.
        Optional<Categoria> existingCategoryWithName = categoriaRepository.findByNome(novoNome);
        if (existingCategoryWithName.isPresent() && !existingCategoryWithName.get().id().equals(id)) {
            System.out.println("Another category with name '" + novoNome + "' already exists (ID: " + existingCategoryWithName.get().id() + ").");
            throw new IllegalArgumentException("Já existe uma categoria com o nome '" + novoNome + "'.");
        }

        // 3. Use o método 'update' explícito do repositório para atualizar o nome
        // Este é o método que você definiu com @Query no CategoriaRepository
        int updatedRows = categoriaRepository.update(id, novoNome);
        System.out.println("Updated " + updatedRows + " rows for category ID: " + id);

        if (updatedRows == 0) {
            // Isso pode acontecer se a categoria foi deletada logo após a busca (cenário de concorrência),
            // ou se o nome enviado é o mesmo do nome atual e o banco não registrou "alteração".
            // No caso de atualização por nome, se o nome for idêntico, 0 linhas podem ser afetadas,
            // mas o findById logo abaixo ainda funcionaria.
            // No entanto, se o findById acima retornou um resultado, updatedRows 0 é incomum,
            // a menos que o nome não tenha realmente mudado.
            throw new RuntimeException("Falha ao atualizar categoria com ID " + id + ". Nenhuma linha afetada.");
        }

        // 4. Retorne a categoria atualizada buscando-a novamente para ter o estado mais recente
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNaoEncontradaException(
                        "Erro ao recuperar categoria atualizada com ID: " + id));
    }
}