package com.unitins.controller;

import com.unitins.model.Usuario;
import com.unitins.service.UsuarioService;
import com.unitins.service.exception.UsuarioNaoEncontradoException;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Validated // Habilita a validação das requisições
@Controller("/api/usuarios") // Define o caminho base para este controlador
@Tag(name = "usuarios") // Tag para documentação Swagger/OpenAPI
public class UsuarioController {

    private final UsuarioService usuarioService; // Injeta o serviço de usuário

    // Construtor para injeção de dependência
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * GET /api/usuarios
     * Lista todos os usuários existentes.
     */
    @Get("/") // Mapeia para requisições GET no caminho base
    @Operation(summary = "Listar todos os usuários",
               description = "Retorna a lista de todos os usuários cadastrados")
    public HttpResponse<?> listarTodos() {
        // Retorna uma resposta HTTP 200 OK com a lista de usuários
        return HttpResponse.ok(usuarioService.buscarTodos());
    }

    /**
     * GET /api/usuarios/{id}
     * Busca um usuário pelo ID informado.
     */
    @Get("/{id}") // Mapeia para requisições GET com um ID na URL
    @Operation(summary = "Buscar usuário por ID",
               description = "Retorna o usuário correspondente ao ID informado")
    public HttpResponse<?> buscarPorId(@PathVariable Long id) {
        // Busca o usuário pelo ID e retorna 200 OK se encontrado, ou 404 Not Found se não
        return usuarioService.buscarPorId(id)
                .map(HttpResponse::ok) // Se Optional contiver um valor, mapeia para 200 OK
                .orElse(HttpResponse.notFound()); // Se Optional estiver vazio, retorna 404 Not Found
    }

    /**
     * POST /api/usuarios
     * Cria um novo usuário.
     */
    @Post("/") // Mapeia para requisições POST no caminho base
    @Operation(summary = "Criar usuário",
               description = "Cria um novo usuário com os dados fornecidos")
    public HttpResponse<?> criar(@Body @Valid Usuario usuario) {
        // Cria o novo usuário usando o serviço e retorna uma resposta HTTP 201 Created
        Usuario novo = usuarioService.salvarUsuario(usuario);
        return HttpResponse.created(novo);
    }

    /**
     * PUT /api/usuarios/{id}
     * Atualiza o usuário existente com o ID informado.
     */
    @Put("/{id}") // Mapeia para requisições PUT com um ID na URL
    @Operation(summary = "Atualizar usuário",
               description = "Atualiza o usuário existente com o ID informado")
    public HttpResponse<?> atualizar(@PathVariable Long id, @Body @Valid Usuario usuario) {
        try {
            // Tenta atualizar o usuário usando o serviço
            Usuario atualizado = usuarioService.atualizarUsuario(id, usuario);
            // Retorna 200 OK com o usuário atualizado se bem-sucedido
            return HttpResponse.ok(atualizado);
        } catch (UsuarioNaoEncontradoException e) {
            // Se o usuário não for encontrado, retorna 404 Not Found
            return HttpResponse.notFound();
        }
    }

    /**
     * DELETE /api/usuarios/{id}
     * Remove o usuário com o ID informado.
     */
    @Delete("/{id}") // Mapeia para requisições DELETE com um ID na URL
    @Operation(summary = "Deletar usuário",
               description = "Remove o usuário com o ID informado")
    public HttpResponse<?> deletar(@PathVariable Long id) {
        try {
            // Tenta deletar o usuário usando o serviço
            usuarioService.deletarUsuario(id);
            // Retorna 204 No Content se bem-sucedido (operação de exclusão que não retorna conteúdo)
            return HttpResponse.noContent();
        } catch (UsuarioNaoEncontradoException e) {
            // Se o usuário não for encontrado, retorna 404 Not Found
            return HttpResponse.notFound();
        }
    }

    // Exemplo de endpoint adicional para buscar por email
    @Get("/email/{email}") // Mapeia para requisições GET com email na URL
    @Operation(summary = "Buscar usuário por E-mail",
               description = "Retorna o usuário correspondente ao E-mail informado")
    public HttpResponse<?> buscarPorEmail(@PathVariable String email) {
         return usuarioService.buscarPorEmail(email)
                .map(HttpResponse::ok) // Se Optional contiver um valor, mapeia para 200 OK
                .orElse(HttpResponse.notFound()); // Se Optional estiver vazio, retorna 404 Not Found
    }
}
