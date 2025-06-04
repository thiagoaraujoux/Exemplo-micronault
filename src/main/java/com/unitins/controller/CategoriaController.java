// src/main/java/com/unitins/controller/CategoriaController.java
package com.unitins.controller;

import com.unitins.model.Categoria;
import com.unitins.model.ApiResponseMessage;
import com.unitins.service.CategoriaService;
import com.unitins.service.exception.CategoriaNaoEncontradaException;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType; // Importar MediaType
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Validated
@Controller("/api/categorias")
@Tag(name = "Categorias", description = "Operações relacionadas a categorias de listas")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    /**
     * GET /api/categorias
     * Lista todas as categorias existentes.
     */
    @Get(produces = MediaType.APPLICATION_JSON) // Removido o "/" e adicionado MediaType
    @Operation(summary = "Listar todas as categorias", description = "Retorna a lista de todas as categorias cadastradas.")
    @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = Categoria.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar listar categorias.")
    public HttpResponse<List<Categoria>> listarTodos() {
        List<Categoria> categorias = StreamSupport.stream(categoriaService.buscarTodos().spliterator(), false)
                .collect(Collectors.toList());
        return HttpResponse.ok(categorias);
    }

    /**
     * GET /api/categorias/{id}
     * Busca uma categoria pelo ID informado.
     */
    @Get("/{id}")
    @Operation(summary = "Buscar categoria por ID", description = "Retorna a categoria correspondente ao ID informado.")
    @ApiResponse(responseCode = "200", description = "Categoria encontrada com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Categoria.class)))
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada para o ID especificado.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar buscar a categoria.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    public HttpResponse<?> buscarPorId(@PathVariable Long id) {
        Optional<Categoria> categoriaOptional = categoriaService.buscarPorId(id);
        if (categoriaOptional.isPresent()) {
            return HttpResponse.ok(categoriaOptional.get());
        } else {
            return HttpResponse.notFound(ApiResponseMessage.error("Categoria não encontrada."));
        }
    }

    /**
     * POST /api/categorias
     * Cria uma nova categoria.
     * Returns the created Categoria on success (201 Created), or an
     * ApiResponseMessage on error (500 Internal Server Error).
     */
    @Post(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Criar nova categoria", description = "Cria uma nova categoria com os dados fornecidos no corpo da requisição.")
    @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Categoria.class)))
    @ApiResponse(responseCode = "400", description = "Requisição inválida (dados ausentes ou mal formatados), validação falhou.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar criar a categoria.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    public HttpResponse<?> criar(@Body @Valid Categoria categoria) {
        try {
            Categoria nova = categoriaService.salvarCategoria(categoria); // Correto para criar
            return HttpResponse.created(nova);
        } catch (IllegalArgumentException e) { // Adicione esta captura se o salvarCategoria puder lançar
            return HttpResponse.badRequest(ApiResponseMessage.error(e.getMessage()));
        } catch (Exception e) {
            return HttpResponse.serverError(ApiResponseMessage.error("Erro ao criar categoria: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/categorias/{id}
     * Atualiza a categoria existente com o ID informado.
     * Returns the updated Categoria on success (200 OK), or an ApiResponseMessage
     * on error (404 Not Found or 500 Internal Server Error).
     */
    @Put("/{id}")
    @Operation(summary = "Atualizar categoria existente", description = "Atualiza os dados de uma categoria existente com base no ID.")
    @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Categoria.class)))
    @ApiResponse(responseCode = "400", description = "Requisição inválida (dados ausentes ou mal formatados), validação falhou.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada para o ID especificado.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar atualizar a categoria.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    public HttpResponse<?> atualizar(@PathVariable Long id, @Body @Valid Categoria categoria) {
        try {
            // AQUI ESTÁ A CORREÇÃO PRINCIPAL: CHAME O MÉTODO DE ATUALIZAÇÃO CORRETO
            Categoria atualizada = categoriaService.atualizarCategoria(id, categoria.nome());
            return HttpResponse.ok(atualizada); // Retorne 200 OK para atualização
        } catch (CategoriaNaoEncontradaException e) {
            return HttpResponse.notFound(ApiResponseMessage.error("Categoria não encontrada: " + e.getMessage()));
        } catch (IllegalArgumentException e) { // Captura exceção de nome duplicado
            return HttpResponse.badRequest(ApiResponseMessage.error(e.getMessage()));
        } catch (Exception e) {
            return HttpResponse.serverError(ApiResponseMessage.error("Erro ao atualizar categoria: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/categorias/{id}
     * Remove a categoria com o ID informado.
     * Returns 204 No Content on success, or an ApiResponseMessage on error (404 Not
     * Found or 500 Internal Server Error).
     */
    @Delete("/{id}")
    @Operation(summary = "Deletar categoria", description = "Remove a categoria com o ID especificado do sistema.")
    @ApiResponse(responseCode = "204", description = "Categoria deletada com sucesso (Nenhum conteúdo para retornar).")
    @ApiResponse(responseCode = "200", description = "Categoria deletada com sucesso (com mensagem no corpo, se aplicável).", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada para o ID especificado (já excluída ou nunca existiu).", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar deletar a categoria.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    public HttpResponse<?> deletar(@PathVariable Long id) {
        try {
            categoriaService.deletarCategoria(id);
            return HttpResponse.noContent(); // Standard for successful DELETE with no body
        } catch (CategoriaNaoEncontradaException e) {
            return HttpResponse.notFound(ApiResponseMessage.error("Categoria não encontrada ou já excluída."));
        } catch (Exception e) {
            return HttpResponse.serverError(ApiResponseMessage.error("Erro ao deletar categoria: " + e.getMessage()));
        }
    }
}