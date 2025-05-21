package com.unitins.service;

import com.unitins.model.Lista;

import java.util.Optional;

public interface ListaService {

    Lista criarLista(Lista lista);

    Iterable<Lista> buscarTodos();

    Optional<Lista> buscarPorId(Long id);

    Iterable<Lista> listarPorUsuario(Long usuarioId);

    Lista salvarLista(Lista lista);  // <-- ADICIONE este método

    void deletarLista(Long id);

    Lista atualizarLista(Long id, Lista lista);
}
