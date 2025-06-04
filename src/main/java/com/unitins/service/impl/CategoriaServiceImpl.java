package com.unitins.service.impl; // Correct package declaration

import com.unitins.model.Categoria;
import com.unitins.repository.CategoriaRepository;
import com.unitins.service.CategoriaService; // Import the service interface
import com.unitins.service.exception.CategoriaNaoEncontradaException;

import jakarta.inject.Singleton; // Anotação para indicar que é um bean singleton
import io.micronaut.transaction.annotation.Transactional; // Anotação de transação do Micronaut

import java.util.Optional;

@Singleton // Micronaut gerencia esta classe como um singleton
public class CategoriaServiceImpl implements CategoriaService { // Implementa a interface do serviço

    private final CategoriaRepository categoriaRepository; // Injeta o repositório

    // Construtor para injeção de dependência
    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional // Marca o método como transacional (escrita)
    public Categoria criarCategoria(Categoria categoria) {
        // Lógica adicional de negócio antes de salvar, se necessário
        // Como Categoria é um record (imutável), save() retornará uma nova instância
        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional(readOnly = true) // Marca o método como transacional (somente leitura)
    public Iterable<Categoria> buscarTodos() {
        return categoriaRepository.findAll(); // Busca todas as categorias
    }

    @Override
    @Transactional(readOnly = true) // Marca o método como transacional (somente leitura)
    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id); // Busca uma categoria pelo ID
    }

    @Override
    @Transactional // Marca o método como transacional (escrita)
    public Categoria salvarCategoria(Categoria categoria) {
        // Implementação para salvar/criar uma categoria
        // Como Categoria é um record (imutável), save() retornará uma nova instância
        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional // Marca o método como transacional (escrita)
    public void deletarCategoria(Long id) {
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);
        if (categoriaOptional.isPresent()) {
            categoriaRepository.delete(categoriaOptional.get()); // Deleta a categoria encontrada
        } else {
            // Lança exceção se a categoria não for encontrada
            throw new CategoriaNaoEncontradaException("Categoria com ID " + id + " não encontrada");
        }
    }

    @Override
    @Transactional // Marca o método como transacional (escrita)
    public Categoria atualizarCategoria(Long id, String novoNome) {
        // 1. Primeiro, verifique se a categoria que se deseja atualizar existe
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);
        if (categoriaOptional.isEmpty()) {
            throw new CategoriaNaoEncontradaException("Categoria com ID " + id + " não encontrada.");
        }

        // 2. Verifique se o 'novoNome' já existe para OUTRA categoria
        Optional<Categoria> existingCategoryWithName = categoriaRepository.findByNome(novoNome);
        if (existingCategoryWithName.isPresent() && !existingCategoryWithName.get().id().equals(id)) {
            // Se encontrou uma categoria com o mesmo nome E o ID é diferente do que estamos atualizando,
            // significa que o nome já está em uso por outra categoria.
            throw new IllegalArgumentException("Já existe uma categoria com o nome '" + novoNome + "'.");
        }

        // 3. Use o método 'update' explícito do repositório para atualizar o nome
        int updatedRows = categoriaRepository.update(id, novoNome);

        // Verifica se a atualização realmente afetou alguma linha no banco
        if (updatedRows == 0) {
            // Isso pode acontecer se a categoria foi deletada logo após a busca (cenário de concorrência)
            // ou se o nome não mudou (o que normalmente não é um erro, mas pode indicar falha na lógica)
            // Lançamos uma exceção para tratar isso como uma falha na operação.
            throw new RuntimeException("Falha ao atualizar categoria com ID " + id + ". Nenhuma linha afetada.");
        }

        // 4. Retorne a categoria atualizada buscando-a novamente para ter o estado mais recente
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNaoEncontradaException(
                        "Erro ao recuperar categoria atualizada com ID: " + id));
    }
}
