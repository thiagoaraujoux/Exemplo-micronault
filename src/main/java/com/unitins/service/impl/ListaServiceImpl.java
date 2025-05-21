package com.unitins.service.impl;

import com.unitins.model.Lista;
import com.unitins.model.Usuario;
import com.unitins.repository.ListaRepository;
import com.unitins.repository.UsuarioRepository;
import com.unitins.service.ListaService;
import com.unitins.service.exception.ListaNaoEncontradaException;
import com.unitins.service.exception.UsuarioNaoEncontradoException;

import jakarta.inject.Singleton;
import io.micronaut.transaction.annotation.Transactional;

import java.util.Optional;

@Singleton
public class ListaServiceImpl implements ListaService {

    private final ListaRepository listaRepository;
    private final UsuarioRepository usuarioRepository; // Injetado para buscar o usuário

    public ListaServiceImpl(ListaRepository listaRepository, UsuarioRepository usuarioRepository) {
        this.listaRepository = listaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public Lista criarLista(Lista lista) {
        // Lógica adicional de negócio antes de salvar, se necessário
        // Por exemplo, garantir que o usuário e a categoria existam antes de salvar a lista
        // Micronaut Data já faz a validação de relacionamentos se eles forem @NotNull no record
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
        // Busca o usuário. Se não encontrar, lança uma exceção de usuário não encontrado.
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário com ID " + usuarioId + " não encontrado."));
        return listaRepository.findByUsuario(usuario);
    }

    @Override
    @Transactional
    public Lista salvarLista(Lista lista) {
        // Para records, 'save' é geralmente usado tanto para criar quanto para atualizar
        // se o ID já existir e for gerenciado por @GeneratedValue.
        return listaRepository.save(lista);
    }

    @Override
    @Transactional
    public void deletarLista(Long id) {
        // Verifica se a lista existe antes de tentar deletar
        Optional<Lista> listaOptional = listaRepository.findById(id);
        if (listaOptional.isPresent()) {
            listaRepository.delete(listaOptional.get());
        } else {
            // Lança exceção se a lista não for encontrada
            throw new ListaNaoEncontradaException("Lista com ID " + id + " não encontrada");
        }
    }

    @Override
    @Transactional
    public Lista atualizarLista(Long id, Lista listaAtualizada) {
        Optional<Lista> listaOptional = listaRepository.findById(id);
        if (listaOptional.isEmpty()) {
            // Lança exceção se a lista não for encontrada
            throw new ListaNaoEncontradaException("Lista com ID " + id + " não encontrada");
        }
        Lista listaExistente = listaOptional.get();

        // Records são imutáveis, então criamos um novo record com os dados atualizados.
        // Mantemos o ID existente e atualizamos os campos com os valores do record 'listaAtualizada'
        // (que é o objeto recebido no corpo da requisição PUT).
        Lista listaParaSalvar = new Lista(
            listaExistente.id(),          // Mantém o ID existente
            listaAtualizada.titulo(),     // Usa o título do record de entrada
            listaAtualizada.descricao(),  // Usa a descrição do record de entrada
            listaAtualizada.dataCriacao(),// Usa a data de criação do record de entrada
            listaAtualizada.usuario(),    // Usa o usuário do record de entrada
            listaAtualizada.categoria()   // Usa a categoria do record de entrada
        );

        // O método save() do CrudRepository para records com @Id e @GeneratedValue
        // geralmente lida com a atualização se o ID já existir.
        return listaRepository.save(listaParaSalvar);
    }
}
