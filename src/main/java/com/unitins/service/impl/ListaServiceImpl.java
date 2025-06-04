package com.unitins.service.impl;

import com.unitins.model.Lista;
import com.unitins.dto.ListaUpdateDTO;
import com.unitins.repository.ListaRepository;
import com.unitins.repository.CategoriaRepository;
import io.micronaut.transaction.annotation.Transactional;
import com.unitins.service.exception.ListaNaoEncontradaException;
import com.unitins.service.ListaService;
import com.unitins.service.exception.CategoriaNaoEncontradaException;

import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
public class ListaServiceImpl implements ListaService {

    private final ListaRepository listaRepository;
    private final CategoriaRepository categoriaRepository;

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
        return listaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional
    public Lista salvarLista(Lista lista) {
        // Este método é mais genérico e pode ser usado para ambos: criação e atualização.
        // Se a 'lista' passada tiver um ID existente, o Micronaut Data (via JPA)
        // tentará fazer um UPDATE. Se não tiver ID (ou for 0), fará um INSERT.
        return listaRepository.save(lista);
    }

    @Override
    @Transactional
    public void deletarLista(Long id) {
        System.out.println("Tentando deletar lista com ID: " + id);
        Optional<Lista> listaOptional = listaRepository.findById(id);

        if (listaOptional.isPresent()) {
            Lista lista = listaOptional.get();
            System.out.println("Lista encontrada! ID: " + lista.id() + ", Título: " + lista.titulo());
            if (lista.usuario() != null) {
                System.out.println("Proprietário da Lista (ID): " + lista.usuario().id() + ", Username: "
                        + lista.usuario().nome());
            } else {
                System.out.println(
                        "Proprietário da Lista: NULL (Isso não deveria acontecer se o save original foi feito com usuário!)");
            }
            listaRepository.delete(lista);
            System.out.println("Lista deletada com sucesso! ID: " + id);
        } else {
            System.out.println("Lista com ID " + id + " NÃO encontrada pelo findById. Lançando exceção.");
            throw new ListaNaoEncontradaException("Lista com ID " + id + " não encontrada");
        }
    }

    @Override
    @Transactional
    public Lista atualizarLista(Long id, ListaUpdateDTO listaUpdateDTO) {
        System.out.println("Attempting to update list with ID: " + id);
        Optional<Lista> listaOptional = listaRepository.findById(id);

        if (listaOptional.isEmpty()) {
            System.out.println("List with ID " + id + " not found in repository.");
            throw new ListaNaoEncontradaException("Lista com ID " + id + " não encontrada para atualização.");
        }

        Lista listaExistente = listaOptional.get();

        String novoTitulo = listaExistente.titulo();
        String novaDescricao = listaExistente.descricao();
        Long novaCategoriaId = listaExistente.categoria() != null ? listaExistente.categoria().id() : null; // Pega o ID da categoria existente

        if (listaUpdateDTO.getTitulo() != null) {
            novoTitulo = listaUpdateDTO.getTitulo();
        }
        if (listaUpdateDTO.getDescricao() != null) {
            novaDescricao = listaUpdateDTO.getDescricao();
        }

        if (listaUpdateDTO.getCategoriaId() != null) {
            // Verifica se a categoria existe antes de tentar associar
            categoriaRepository.findById(listaUpdateDTO.getCategoriaId())
                    .orElseThrow(() -> new CategoriaNaoEncontradaException(
                            "Categoria com ID " + listaUpdateDTO.getCategoriaId() + " não encontrada."));
            novaCategoriaId = listaUpdateDTO.getCategoriaId();
        } else {
            // Se o ID da categoria for explicitamente NULL no DTO, defina o ID da categoria como NULL
            novaCategoriaId = null;
        }

        // Use o método de atualização explícito
        int updatedCount = listaRepository.update(id, novoTitulo, novaDescricao, novaCategoriaId);

        if (updatedCount == 0) {
            // Isso deve acontecer se o registro não foi encontrado para atualização,
            // embora já tenhamos feito o findById antes. É um fallback.
            throw new ListaNaoEncontradaException("Lista com ID " + id + " não encontrada ou não pôde ser atualizada.");
        }

        // Após a atualização bem-sucedida, busque a lista atualizada do banco para retornar o Record completo
        return listaRepository.findById(id)
                .orElseThrow(() -> new ListaNaoEncontradaException("Erro ao recuperar lista atualizada com ID: " + id));
    }
}