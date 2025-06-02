package com.unitins.controller;

import com.unitins.model.Lista;
import com.unitins.dto.ListaUpdateDTO;
import com.unitins.model.ApiResponseMessage;
import com.unitins.service.ListaService;
import com.unitins.service.exception.CategoriaNaoEncontradaException;
import com.unitins.service.exception.ListaNaoEncontradaException;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
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
@Controller("/api/listas")
@Tag(name = "Listas", description = "Operações relacionadas a listas de tarefas ou itens")
public class ListaController {

    private final ListaService listaService;

    public ListaController(ListaService listaService) {
        this.listaService = listaService;
    }

    /**
     * GET /api/listas
     * Lista todas as listas existentes.
     */
    @Get(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Listar todas as listas", description = "Retorna a lista de todas as listas cadastradas.")
    @ApiResponse(responseCode = "200", description = "Lista de listas retornada com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = Lista.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar listar listas.")
    public HttpResponse<List<Lista>> listarTodos() {
        List<Lista> listas = StreamSupport.stream(listaService.buscarTodos().spliterator(), false)
                .collect(Collectors.toList());
        return HttpResponse.ok(listas);
    }

    /**
     * GET /api/listas/{id}
     * Busca uma lista pelo ID informado.
     */
    @Get("/{id}")
    @Operation(summary = "Buscar lista por ID", description = "Retorna a lista correspondente ao ID informado.")
    @ApiResponse(responseCode = "200", description = "Lista encontrada com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Lista.class)))
    @ApiResponse(responseCode = "404", description = "Lista não encontrada para o ID especificado.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar buscar a lista.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    public HttpResponse<?> buscarPorId(@PathVariable Long id) {
        Optional<Lista> listaOptional = listaService.buscarPorId(id);
        if (listaOptional.isPresent()) {
            return HttpResponse.ok(listaOptional.get());
        } else {
            return HttpResponse.notFound(ApiResponseMessage.error("Lista não encontrada."));
        }
    }

    /**
     * POST /api/listas
     * Cria uma nova lista.
     * Returns the created Lista on success (201 Created), or an ApiResponseMessage
     * on error (500 Internal Server Error).
     */
    @Post(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Criar nova lista", description = "Cria uma nova lista com os dados fornecidos no corpo da requisição.")
    @ApiResponse(responseCode = "201", description = "Lista criada com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Lista.class)))
    @ApiResponse(responseCode = "400", description = "Requisição inválida (dados ausentes ou mal formatados), validação falhou.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar criar a lista.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    public HttpResponse<?> criar(@Body @Valid Lista lista) {
        try {
            Lista nova = listaService.salvarLista(lista);
            return HttpResponse.created(nova);
        } catch (Exception e) {
            return HttpResponse.serverError(ApiResponseMessage.error("Erro ao criar lista: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/listas/{id}
     * Atualiza a lista existente com o ID informado.
     * Returns the updated Lista on success (200 OK), or an ApiResponseMessage on
     * error (404 Not Found or 500 Internal Server Error).
     */
    @Put("/{id}")
    @Operation(summary = "Atualizar lista existente", description = "Atualiza os dados de uma lista existente com base no ID.")
    @ApiResponse(responseCode = "200", description = "Lista atualizada com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Lista.class)))
    @ApiResponse(responseCode = "400", description = "Requisição inválida (dados ausentes ou mal formatados), validação falhou.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    @ApiResponse(responseCode = "404", description = "Lista não encontrada para o ID especificado.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar atualizar a lista.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    public HttpResponse<?> atualizar(@PathVariable Long id, @Body @Valid ListaUpdateDTO dto) {
        try {
            Lista atualizada = listaService.atualizarLista(id, dto);
            return HttpResponse.ok(atualizada);
        } catch (ListaNaoEncontradaException e) {
            return HttpResponse
                    .notFound(ApiResponseMessage.error("Lista não encontrada para atualização: " + e.getMessage()));
        } catch (CategoriaNaoEncontradaException e) {
            return HttpResponse.badRequest(ApiResponseMessage.error("Erro na categoria: " + e.getMessage()));
        } catch (Exception e) {
            return HttpResponse.serverError(ApiResponseMessage.error("Erro ao atualizar lista: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/listas/{id}
     * Remove a lista com o ID informado.
     * Returns 204 No Content on success, or an ApiResponseMessage on error (404 Not
     * Found or 500 Internal Server Error).
     */
    @Delete("/{id}")
    @Operation(summary = "Deletar lista", description = "Remove a lista com o ID especificado do sistema.")
    @ApiResponse(responseCode = "204", description = "Lista deletada com sucesso (Nenhum conteúdo para retornar).")
    @ApiResponse(responseCode = "404", description = "Lista não encontrada para o ID especificado (já excluída ou nunca existiu).", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar deletar a lista.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseMessage.class)))
    public HttpResponse<?> deletar(@PathVariable Long id) {
        try {
            listaService.deletarLista(id);
            return HttpResponse.noContent(); // Standard for successful DELETE with no body
        } catch (ListaNaoEncontradaException e) {
            return HttpResponse.notFound(ApiResponseMessage.error("Lista não encontrada ou já excluída."));
        } catch (Exception e) {
            return HttpResponse.serverError(ApiResponseMessage.error("Erro ao deletar lista: " + e.getMessage()));
        }
    }
}
