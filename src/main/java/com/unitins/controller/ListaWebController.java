// src/main/java/com/unitins/controller/ListaWebController.java
package com.unitins.controller;

import com.unitins.model.Categoria;
import com.unitins.model.Lista;
import com.unitins.model.Usuario;
import com.unitins.service.CategoriaService;
import com.unitins.service.ListaService;
import com.unitins.service.UsuarioService;
import com.unitins.service.exception.CategoriaNaoEncontradaException;
import com.unitins.service.exception.ListaNaoEncontradaException; // Importar ListaNaoEncontradaException
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.views.View;
import io.micronaut.session.Session;
import jakarta.validation.ConstraintViolationException;
import io.micronaut.http.HttpStatus; // Importar HttpStatus para usar HttpStatus.FORBIDDEN

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller("/listas") // Define o caminho base para este controlador web
public class ListaWebController {

    private final ListaService listaService;
    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;

    // Construtor para injeção de dependência
    public ListaWebController(ListaService listaService, UsuarioService usuarioService, CategoriaService categoriaService) {
        this.listaService = listaService;
        this.usuarioService = usuarioService;
        this.categoriaService = categoriaService;
    }

    /**
     * GET /listas
     * Exibe a página principal de listas, mostrando todas as listas do usuário logado e o formulário de criação.
     *
     * @param session A sessão atual para obter o ID do usuário logado.
     * @param erro Parâmetro de query opcional para exibir mensagens de erro.
     * @param sucesso Parâmetro de query opcional para exibir mensagens de sucesso.
     * @return Um HttpResponse contendo o modelo para o template Thymeleaf 'lista.html' ou um redirecionamento.
     */
    @Get
    @Produces(MediaType.TEXT_HTML)
    @View("lista") // Renderiza o template src/main/resources/views/lista.html
    public HttpResponse<?> showListas(Session session,
                                      @QueryValue Optional<String> erro,
                                      @QueryValue Optional<String> sucesso) {
        Map<String, Object> model = new HashMap<>();

        Long usuarioId = session.get("usuarioId", Long.class).orElse(null);

        if (usuarioId == null) {
            try {
                return HttpResponse.redirect(new URI("/login?erro=nao_logado"));
            } catch (URISyntaxException e) {
                return HttpResponse.serverError("Erro interno ao redirecionar para login.");
            }
        }

        // Adiciona as listas do usuário logado ao modelo
        model.put("listas", listaService.listarPorUsuario(usuarioId));

        // Adiciona todas as categorias ao modelo para popular o dropdown no formulário de criação/edição
        model.put("categorias", categoriaService.buscarTodos());

        // Adiciona mensagens de erro/sucesso ao modelo para que o JS no frontend possa lê-las
        erro.ifPresent(s -> model.put("erro", s));
        sucesso.ifPresent(s -> model.put("sucesso", s));

        return HttpResponse.ok(model);
    }

    /**
     * POST /listas
     * Processa a criação de uma nova lista a partir do formulário HTML.
     *
     * @param form Mapa contendo os dados do formulário (titulo, descricao, categoriaId).
     * @param session A sessão atual para obter o ID do usuário logado.
     * @return Um redirecionamento HTTP para a página de listas com parâmetro de sucesso/erro.
     */
    @Post(consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> criarLista(@Body Map<String, String> form, Session session) {
        String titulo = form.get("titulo");
        String descricao = form.get("descricao");
        Long categoriaId = Optional.ofNullable(form.get("categoriaId"))
                                   .filter(s -> !s.isEmpty())
                                   .map(Long::valueOf)
                                   .orElse(null);

        Long usuarioId = session.get("usuarioId", Long.class).orElse(null);

        if (usuarioId == null) {
            try {
                return HttpResponse.redirect(new URI("/login?erro=nao_logado"));
            } catch (URISyntaxException e) {
                return HttpResponse.serverError("Erro interno ao redirecionar para login.");
            }
        }

        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado na sessão."));

            Categoria categoria = null;
            if (categoriaId != null) {
                categoria = categoriaService.buscarPorId(categoriaId)
                        .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria com ID " + categoriaId + " não encontrada."));
            }

            Lista novaLista = new Lista(null, titulo, descricao, LocalDate.now(), usuario, categoria);

            listaService.salvarLista(novaLista);

            try {
                return HttpResponse.redirect(new URI("/listas?sucesso=lista_criada"));
            } catch (URISyntaxException ex) {
                return HttpResponse.serverError("Erro ao construir URI de redirecionamento de sucesso.");
            }
        } catch (ConstraintViolationException e) {
            try {
                return HttpResponse.redirect(new URI("/listas?erro=validacao"));
            } catch (URISyntaxException ex) {
                return HttpResponse.serverError("Erro ao construir URI de redirecionamento de erro de validação.");
            }
        } catch (CategoriaNaoEncontradaException e) {
            try {
                return HttpResponse.redirect(new URI("/listas?erro=categoria_nao_encontrada"));
            } catch (URISyntaxException ex) {
                return HttpResponse.serverError("Erro ao construir URI de redirecionamento de categoria não encontrada.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return HttpResponse.redirect(new URI("/listas?erro=inesperado"));
            } catch (URISyntaxException ex) {
                return HttpResponse.serverError("Erro ao construir URI de redirecionamento de erro inesperado.");
            }
        }
    }

    /**
     * PUT /listas/{id}
     * Processa a atualização de uma lista existente.
     *
     * @param id O ID da lista a ser atualizada.
     * @param form Mapa contendo os dados atualizados do formulário (titulo, descricao, categoriaId).
     * @param session A sessão atual para obter o ID do usuário logado.
     * @return Um HttpResponse indicando sucesso ou erro.
     */
    @Put(uri = "/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> atualizarLista(@PathVariable Long id, @Body Map<String, String> form, Session session) {
        String titulo = form.get("titulo");
        String descricao = form.get("descricao");
        Long categoriaId = Optional.ofNullable(form.get("categoriaId"))
                                   .filter(s -> !s.isEmpty())
                                   .map(Long::valueOf)
                                   .orElse(null);

        Long usuarioId = session.get("usuarioId", Long.class).orElse(null);

        if (usuarioId == null) {
            return HttpResponse.unauthorized(); // Ou redirecionar para login
        }

        try {
            // Primeiro, busca a lista existente para verificar o proprietário
            Lista listaExistente = listaService.buscarPorId(id)
                    .orElseThrow(() -> new ListaNaoEncontradaException("Lista com ID " + id + " não encontrada."));

            // Verifica se o usuário logado é o proprietário da lista
            if (!listaExistente.usuario().id().equals(usuarioId)) {
                // Retorna 403 Forbidden
                return HttpResponse.status(HttpStatus.FORBIDDEN, "Você não tem permissão para editar esta lista.");
            }

            Usuario usuario = usuarioService.buscarPorId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no sistema."));

            Categoria categoria = null;
            if (categoriaId != null) {
                categoria = categoriaService.buscarPorId(categoriaId)
                        .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria com ID " + categoriaId + " não encontrada."));
            }

            // Cria uma nova instância de Lista com os dados atualizados (records são imutáveis)
            Lista listaAtualizada = new Lista(id, titulo, descricao, listaExistente.dataCriacao(), usuario, categoria);

            listaService.atualizarLista(id, listaAtualizada);

            return HttpResponse.ok().body("Lista atualizada com sucesso."); // Retorna um OK simples para o JS
        } catch (ListaNaoEncontradaException e) {
            return HttpResponse.notFound();
        } catch (ConstraintViolationException e) {
            return HttpResponse.badRequest("Erro de validação: " + e.getMessage());
        } catch (CategoriaNaoEncontradaException e) {
            return HttpResponse.badRequest("Categoria não encontrada.");
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResponse.serverError("Erro inesperado ao atualizar a lista.");
        }
    }

    /**
     * DELETE /listas/{id}
     * Processa a exclusão de uma lista.
     *
     * @param id O ID da lista a ser excluída.
     * @param session A sessão atual para obter o ID do usuário logado.
     * @return Um HttpResponse indicando sucesso ou erro.
     */
    @Delete("/{id}")
    public HttpResponse<?> deletarLista(@PathVariable Long id, Session session) {
        Long usuarioId = session.get("usuarioId", Long.class).orElse(null);

        if (usuarioId == null) {
            return HttpResponse.unauthorized(); // Ou redirecionar para login
        }

        try {
            // Primeiro, busca a lista existente para verificar o proprietário
            Lista listaExistente = listaService.buscarPorId(id)
                    .orElseThrow(() -> new ListaNaoEncontradaException("Lista com ID " + id + " não encontrada."));

            // Verifica se o usuário logado é o proprietário da lista
            if (!listaExistente.usuario().id().equals(usuarioId)) {
                // Retorna 403 Forbidden
                return HttpResponse.status(HttpStatus.FORBIDDEN, "Você não tem permissão para deletar esta lista.");
            }

            listaService.deletarLista(id);
            return HttpResponse.noContent(); // Retorna 204 No Content para sucesso na exclusão
        } catch (ListaNaoEncontradaException e) {
            return HttpResponse.notFound();
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResponse.serverError("Erro inesperado ao deletar a lista.");
        }
    }
}
