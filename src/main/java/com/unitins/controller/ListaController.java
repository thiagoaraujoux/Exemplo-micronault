package com.unitins.controller;

import com.unitins.model.Lista;
import com.unitins.service.ListaService;
import com.unitins.service.exception.ListaNaoEncontradaException;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Validated
@Controller("/api/listas")
@Tag(name = "listas")
public class ListaController {

    private final ListaService listaService;

    public ListaController(ListaService listaService) {
        this.listaService = listaService;
    }

    /**
     * GET /api/listas
     * Lista todas as listas existentes.
     */
    @Get("/")
    @Operation(summary = "Listar todas as listas",
               description = "Retorna a lista de todas as listas cadastradas")
    public HttpResponse<?> listarTodos() {
        return HttpResponse.ok(listaService.buscarTodos());
    }

    /**
     * GET /api/listas/{id}
     * Busca uma lista pelo ID informado.
     */
    @Get("/{id}")
    @Operation(summary = "Buscar lista por ID",
               description = "Retorna a lista correspondente ao ID informado")
    public HttpResponse<?> buscarPorId(@PathVariable Long id) {
        return listaService.buscarPorId(id)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    /**
     * POST /api/listas
     * Cria uma nova lista.
     */
    @Post("/")
    @Operation(summary = "Criar lista",
               description = "Cria uma nova lista com os dados fornecidos")
    public HttpResponse<?> criar(@Body @Valid Lista lista) {
        Lista nova = listaService.salvarLista(lista);
        return HttpResponse.created(nova);
    }

    /**
     * PUT /api/listas/{id}
     * Atualiza a lista existente com o ID informado.
     */
    @Put("/{id}")
    @Operation(summary = "Atualizar lista",
               description = "Atualiza a lista existente com o ID informado")
    public HttpResponse<?> atualizar(@PathVariable Long id, @Body @Valid Lista lista) {
        try {
            Lista atualizada = listaService.atualizarLista(id, lista);
            return HttpResponse.ok(atualizada);
        } catch (ListaNaoEncontradaException e) {
            return HttpResponse.notFound();
        }
    }

    /**
     * DELETE /api/listas/{id}
     * Remove a lista com o ID informado.
     */
    @Delete("/{id}")
    @Operation(summary = "Deletar lista",
               description = "Remove a lista com o ID informado")
    public HttpResponse<?> deletar(@PathVariable Long id) {
        try {
            listaService.deletarLista(id);
            return HttpResponse.noContent();
        } catch (ListaNaoEncontradaException e) {
            return HttpResponse.notFound();
        }
    }
}
