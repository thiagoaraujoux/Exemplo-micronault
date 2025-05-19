package com.unitins.controller;


import com.unitins.model.Livro;
import com.unitins.service.LivroNaoEncontradoException;
import com.unitins.service.LivroService;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Validated
@Controller("/api/livros")
@Tag(name = "livros")
public class LivroController {

    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    /**
     * GET /api/livros
     * Lista todos os livros existentes.
     */
    @Get("/")
    @Operation(summary = "Listar todos os livros", 
               description = "Retorna a lista de todos os livros cadastrados")
    public HttpResponse<?> listarTodos() {
        return HttpResponse.ok(livroService.buscarTodos());
    }

    /**
     * GET /api/livros/{id}
     * Busca um livro pelo ID informado.
     */
    @Get("/{id}")
    @Operation(summary = "Buscar livro por ID", 
               description = "Retorna o livro correspondente ao ID informado")
               public HttpResponse<?> buscarPorId(@PathVariable Long id) {
                return livroService.buscarPorId(id)
                    .map(HttpResponse::ok)
                    .orElse(HttpResponse.notFound());
            }
    /**
     * POST /api/livros
     * Cria um novo livro.
     */
    @Post("/")
    @Operation(summary = "Criar livro", 
               description = "Cria um novo livro com os dados fornecidos")
    public HttpResponse<?> criar(@Body @Valid Livro livro) {
        Livro novo = livroService.salvarLivro(livro);
        return HttpResponse.created(novo); // 201 Created com o recurso criado
    }

    /**
     * PUT /api/livros/{id}
     * Atualiza o livro existente com o ID informado.
     */
    @Put("/{id}")
    @Operation(summary = "Atualizar livro", 
               description = "Atualiza o livro existente com o ID informado")
    public HttpResponse<?> atualizar(@PathVariable Long id, @Body @Valid Livro livro) {
        try {
            Livro atualizado = livroService.atualizarLivro(id, livro);
            return HttpResponse.ok(atualizado);
        } catch (LivroNaoEncontradoException e) {
            return HttpResponse.notFound();
        }
    }

    /**
     * DELETE /api/livros/{id}
     * Remove o livro com o ID informado.
     */
    @Delete("/{id}")
    @Operation(summary = "Deletar livro", 
               description = "Remove o livro com o ID informado")
    public HttpResponse<?> deletar(@PathVariable Long id) {
        try {
            livroService.deletarLivro(id);
            return HttpResponse.noContent(); // 204 No Content
        } catch (LivroNaoEncontradoException e) {
            return HttpResponse.notFound();
        }
    }
}
