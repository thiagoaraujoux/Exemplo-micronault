package com.unitins.service.impl;


import com.unitins.model.Lista;
import com.unitins.model.Categoria;
import com.unitins.repository.ListaRepository;
// import com.unitins.repository.UsuarioRepository; // Removido: UsuarioRepository não é mais necessário aqui
import com.unitins.repository.CategoriaRepository;
import io.micronaut.transaction.annotation.Transactional;
import com.unitins.service.exception.ListaNaoEncontradaException;
import com.unitins.service.ListaService;
// import com.unitins.service.exception.UsuarioNaoEncontradoException; // Removido: Exceção de usuário não é mais lançada aqui
import com.unitins.service.exception.CategoriaNaoEncontradaException;

import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
public class ListaServiceImpl implements ListaService {

    private final ListaRepository listaRepository;
    // private final UsuarioRepository usuarioRepository; // Removido: Campo não utilizado
    private final CategoriaRepository categoriaRepository;

    // Construtor corrigido para remover UsuarioRepository
    public ListaServiceImpl(ListaRepository listaRepository, CategoriaRepository categoriaRepository) {
        this.listaRepository = listaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional
    public Lista criarLista(Lista lista) {
        return listaRepository.save(lista);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<Lista> buscarTodos() {
        return listaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Lista> buscarPorId(Long id) {
        return listaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<Lista> listarPorUsuario(Long usuarioId) {
        // Simplificado: Usando findByUsuarioId diretamente do ListaRepository.
        // Isso remove a necessidade de injetar UsuarioRepository neste serviço para esta operação.
        return listaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional
    public Lista salvarLista(Lista lista) {
        return listaRepository.save(lista);
    }

    @Override
    @Transactional
    public void deletarLista(Long id) {
        Optional<Lista> listaOptional = listaRepository.findById(id);
        if (listaOptional.isPresent()) {
            listaRepository.delete(listaOptional.get());
        } else {
            throw new ListaNaoEncontradaException("Lista com ID " + id + " não encontrada");
        }
    }

    @Override
    @Transactional
    public Lista atualizarLista(Long id, Lista listaAtualizada) {
        Optional<Lista> listaOptional = listaRepository.findById(id);
        if (listaOptional.isEmpty()) {
            throw new ListaNaoEncontradaException("Lista com ID " + id + " não encontrada");
        }
        Lista listaExistente = listaOptional.get();

        Categoria categoriaParaSalvar = null;
        if (listaAtualizada.categoria() != null && listaAtualizada.categoria().id() != null) {
            categoriaParaSalvar = categoriaRepository.findById(listaAtualizada.categoria().id())
                                    .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria com ID " + listaAtualizada.categoria().id() + " não encontrada."));
        } else {
            categoriaParaSalvar = listaExistente.categoria();
        }

        Lista listaParaSalvar = new Lista(
            listaExistente.id(),
            listaAtualizada.titulo(),
            listaAtualizada.descricao(),
            listaExistente.dataCriacao(),
            listaExistente.usuario(),
            categoriaParaSalvar
        );

        return listaRepository.update(listaParaSalvar);
    }
}
