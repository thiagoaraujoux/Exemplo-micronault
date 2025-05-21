package com.unitins.controller;

import com.unitins.model.Categoria;
import com.unitins.service.CategoriaService;
import com.unitins.service.exception.CategoriaNaoEncontradaException;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Validated
@Controller("/api/categorias")
@Tag(name = "categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    /**
     * GET /api/categorias
     * Lista todas as categorias existentes.
     */
    @Get("/")
    @Operation(summary = "Listar todas as categorias",
               description = "Retorna a lista de todas as categorias cadastradas")
    public HttpResponse<?> listarTodos() {
        return HttpResponse.ok(categoriaService.buscarTodos());
    }

    /**
     * GET /api/categorias/{id}
     * Busca uma categoria pelo ID informado.
     */
    @Get("/{id}")
    @Operation(summary = "Buscar categoria por ID",
               description = "Retorna a categoria correspondente ao ID informado")
    public HttpResponse<?> buscarPorId(@PathVariable Long id) {
        return categoriaService.buscarPorId(id)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    /**
     * POST /api/categorias
     * Cria uma nova categoria.
     */
    @Post("/")
    @Operation(summary = "Criar categoria",
               description = "Cria uma nova categoria com os dados fornecidos")
    public HttpResponse<?> criar(@Body @Valid Categoria categoria) {
        Categoria nova = categoriaService.salvarCategoria(categoria);
        return HttpResponse.created(nova);
    }

    /**
     * PUT /api/categorias/{id}
     * Atualiza a categoria existente com o ID informado.
     */
    @Put("/{id}")
    @Operation(summary = "Atualizar categoria",
               description = "Atualiza a categoria existente com o ID informado")
    public HttpResponse<?> atualizar(@PathVariable Long id, @Body @Valid Categoria categoria) {
        try {
            Categoria atualizada = categoriaService.atualizarCategoria(id, categoria);
            return HttpResponse.ok(atualizada);
        } catch (CategoriaNaoEncontradaException e) {
            return HttpResponse.notFound();
        }
    }

    /**
     * DELETE /api/categorias/{id}
     * Remove a categoria com o ID informado.
     */
    @Delete("/{id}")
    @Operation(summary = "Deletar categoria",
               description = "Remove a categoria com o ID informado")
    public HttpResponse<?> deletar(@PathVariable Long id) {
        try {
            categoriaService.deletarCategoria(id);
            return HttpResponse.noContent();
        } catch (CategoriaNaoEncontradaException e) {
            return HttpResponse.notFound();
        }
    }
}
