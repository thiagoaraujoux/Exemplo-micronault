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
    public Categoria atualizarCategoria(Long id, Categoria categoriaAtualizada) {
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);
        if (categoriaOptional.isPresent()) {
            Categoria categoriaExistente = categoriaOptional.get();
            // Records são imutáveis, então criamos um novo record com os dados atualizados.
            // Mantemos o ID existente e atualizamos o nome com o valor do record de entrada.
            Categoria categoriaParaSalvar = new Categoria(
                categoriaExistente.id(), // Mantém o ID existente (usando o accessor do record)
                categoriaAtualizada.nome() // Usa o nome do record de entrada (usando o accessor do record)
            );

            // O método save() do CrudRepository para records com @Id e @GeneratedValue
            // geralmente lida com a atualização se o ID já existir.
            return categoriaRepository.save(categoriaParaSalvar);
        } else {
            // Lança exceção se a categoria não for encontrada
            throw new CategoriaNaoEncontradaException("Categoria com ID " + id + " não encontrada");
        }
    }
}
