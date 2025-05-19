package com.unitins.service.impl;

import io.micronaut.transaction.annotation.Transactional; // Para gerenciamento de transações
import jakarta.inject.Singleton;
import java.util.Optional;

import com.unitins.model.Livro;
import com.unitins.repository.LivroRepository;
import com.unitins.service.IsbnJaCadastradoException;
import com.unitins.service.LivroNaoEncontradoException;
import com.unitins.service.LivroService;

@Singleton // Define como um bean singleton gerenciado pelo Micronaut
public class LivroServiceImpl implements LivroService {
    private final LivroRepository livroRepository;

    // Injeção de dependência via construtor
    public LivroServiceImpl(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @Override
    @Transactional // As operações de escrita devem ser transacionais
    public Livro salvarLivro(Livro livro) {
        // Verifica se já existe um livro com o mesmo ISBN, ignorando o
        // próprio livro se for uma atualização
        Optional<Livro> existente = livroRepository.findByIsbn(livro.isbn());
        if (existente.isPresent() && (livro.id() == null
                || !existente.get().id().equals(livro.id()))) {

            throw new IsbnJaCadastradoException("ISBN " + livro.isbn() + " já cadastrado para outro livro.");
        }
        return livroRepository.save(livro);
    }

    @Override
    @Transactional(readOnly = true) // Operações de leitura podem ser marcadas como readOnly
    public Optional<Livro> buscarPorId(Long id) {
        return livroRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<Livro> buscarTodos() {
        return livroRepository.findAll();
    }

    @Override
    @Transactional
    public Livro atualizarLivro(Long id, Livro livroParaAtualizar) throws LivroNaoEncontradoException {
        Livro livroExistente = livroRepository.findById(id)
                .orElseThrow(() -> new LivroNaoEncontradoException("Livro com ID " + id + " não encontrado."));
        // Verifica duplicidade de ISBN para outro livro
        Optional<Livro> outroLivroComMesmoIsbn = livroRepository.findByIsbn(livroParaAtualizar.isbn());
        if (outroLivroComMesmoIsbn.isPresent()
                && !outroLivroComMesmoIsbn.get().id().equals(id)) {
            throw new IsbnJaCadastradoException("ISBN " +
                    livroParaAtualizar.isbn() + " já cadastrado para outro livro.");
        }
        // Cria um novo record Livro com o ID existente e os dados atualizados
        Livro livroAtualizado = new Livro(
                livroExistente.id(),

                livroParaAtualizar.titulo(),
                livroParaAtualizar.autor(),
                livroParaAtualizar.isbn());
        return livroRepository.update(livroAtualizado); // Micronaut Data usa save() para criar e update() para
                                                        // atualizar
    }

    @Override
    @Transactional
    public void deletarLivro(Long id) throws LivroNaoEncontradoException {
        if (!livroRepository.existsById(id)) {
            throw new LivroNaoEncontradoException("Livro com ID " + id + " não encontrado para deleção.");
        }
        livroRepository.deleteById(id);
    }
}