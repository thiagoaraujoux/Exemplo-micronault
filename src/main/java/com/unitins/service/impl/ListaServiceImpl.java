package com.unitins.service.impl;

import com.unitins.model.Lista;
import com.unitins.dto.ListaUpdateDTO;
import com.unitins.model.Categoria;
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
        // Simplificado: Usando findByUsuarioId diretamente do ListaRepository.
        // Isso remove a necessidade de injetar UsuarioRepository neste serviço para
        // esta operação.
        return listaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional
    public Lista salvarLista(Lista lista) {
        // This method already uses save, which is correct for both creation and update.
        return listaRepository.save(lista);
    }

    @Override
    @Transactional
    public void deletarLista(Long id) {
        System.out.println("Tentando deletar lista com ID: " + id); // Log do ID

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
        System.out.println("Attempting to update list with ID: " + id); // Log the ID being processed
        Optional<Lista> listaOptional = listaRepository.findById(id);
        if (listaOptional.isEmpty()) {
            System.out.println("List with ID " + id + " not found in repository."); // Log if not found
            throw new ListaNaoEncontradaException("Lista com ID " + id + " não encontrada para atualização.");
        }
        Lista listaExistente = listaOptional.get();
        System.out.println("List found with ID: " + listaExistente.id() + ", Title: " + listaExistente.titulo()); // Log if found

        // Get current values from the existing record
        String novoTitulo = listaExistente.titulo();
        String novaDescricao = listaExistente.descricao();
        Categoria novaCategoria = listaExistente.categoria();

        // Update values if they are provided in the DTO
        if (listaUpdateDTO.getTitulo() != null) {
            novoTitulo = listaUpdateDTO.getTitulo();
        }
        if (listaUpdateDTO.getDescricao() != null) {
            novaDescricao = listaUpdateDTO.getDescricao();
        }

        // Handle category update
        if (listaUpdateDTO.getCategoriaId() != null) {
            // A category ID was provided, try to find it
            novaCategoria = categoriaRepository.findById(listaUpdateDTO.getCategoriaId())
                    .orElseThrow(() -> new CategoriaNaoEncontradaException(
                            "Categoria com ID " + listaUpdateDTO.getCategoriaId() + " não encontrada."));
        } else {
            // No category ID provided (or null), set category to null
            novaCategoria = null;
        }

        // Create a NEW Lista record with updated values.
        // For immutable fields like 'id', 'dataCriacao', 'usuario', use the values from
        // 'listaExistente'.
        Lista listaParaSalvar = new Lista(
                listaExistente.id(), // Keep the original ID
                novoTitulo, // Use the potentially updated title
                novaDescricao, // Use the potentially updated description
                listaExistente.dataCriacao(), // Keep the original creation date
                listaExistente.usuario(), // Keep the original user (owner)
                novaCategoria // Use the potentially updated category
        );

        // Use 'save' instead of 'update'. Micronaut Data's 'save' method handles both inserts and updates.
        return listaRepository.save(listaParaSalvar);
    }
}
