// src/main/java/com/unitins/controller/ListaWebController.java
package com.unitins.controller;

import com.unitins.model.Categoria;
import com.unitins.model.Lista;
import com.unitins.model.Usuario;
import com.unitins.service.CategoriaService;
import com.unitins.service.ListaService;
import com.unitins.service.UsuarioService;
import com.unitins.service.exception.CategoriaNaoEncontradaException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.views.View;
import io.micronaut.session.Session;
import jakarta.validation.ConstraintViolationException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate; // Consider LocalDateTime if your Lista model uses it
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller("/listas") // Base path for this web controller
public class ListaWebController {

    private final ListaService listaService;
    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;

    public ListaWebController(ListaService listaService, UsuarioService usuarioService, CategoriaService categoriaService) {
        this.listaService = listaService;
        this.usuarioService = usuarioService;
        this.categoriaService = categoriaService;
    }

    /**
     * GET /listas
     * Displays the main lists page, showing all lists for the logged-in user and the creation form.
     *
     * @param session The current session to get the logged-in user's ID.
     * @param erro Optional query parameter to display error messages.
     * @param sucesso Optional query parameter to display success messages.
     * @return An HttpResponse containing the model for the 'lista.html' Thymeleaf template or a redirect.
     */
    @Get
    @Produces(MediaType.TEXT_HTML)
    @View("lista") // Renders the template src/main/resources/views/listas.html (corrected to 'listas' as per HTML file name)
    public HttpResponse<?> showListas(Session session,
                                      @QueryValue Optional<String> erro,
                                      @QueryValue Optional<String> sucesso) {
        Map<String, Object> model = new HashMap<>();

        // Retrieve user ID from session
        Long usuarioId = session.get("usuarioId", Long.class).orElse(null);
        String currentUserName = session.get("currentUserName", String.class).orElse("Usuário"); // Assuming you store username in session too

        if (usuarioId == null) {
            try {
                return HttpResponse.redirect(new URI("/login?erro=nao_logado"));
            } catch (URISyntaxException e) {
                // Log the exception for debugging
                e.printStackTrace();
                return HttpResponse.serverError("Internal error redirecting to login.");
            }
        }

        // Add logged-in user's ID and Name to the model for Thymeleaf (used by JavaScript)
        model.put("currentUserId", usuarioId);
        model.put("currentUserName", currentUserName);


        // Add lists for the logged-in user to the model
        model.put("listas", listaService.listarPorUsuario(usuarioId));

        // Add all categories to the model to populate the dropdown
        model.put("categorias", categoriaService.buscarTodos());

        // Add error/success messages to the model
        erro.ifPresent(s -> model.put("erro", s));
        sucesso.ifPresent(s -> model.put("sucesso", s));

        return HttpResponse.ok(model);
    }

    /**
     * POST /listas
     * Processes the creation of a new list from the HTML form.
     *
     * @param form Map containing form data (titulo, descricao, categoriaId).
     * @param session The current session to get the logged-in user's ID.
     * @return An HTTP redirect to the lists page with success/error parameter.
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
                e.printStackTrace();
                return HttpResponse.serverError("Internal error redirecting to login.");
            }
        }

        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Logged-in user not found in the system.")); // More specific error message

            Categoria categoria = null;
            if (categoriaId != null) {
                categoria = categoriaService.buscarPorId(categoriaId)
                        .orElseThrow(() -> new CategoriaNaoEncontradaException("Category with ID " + categoriaId + " not found."));
            }

            // If your Lista model uses LocalDateTime, consider LocalDate.now() or ensure consistency.
            // If @DateCreated handles it, you might pass null or remove from constructor.
            Lista novaLista = new Lista(null, titulo, descricao, LocalDate.now(), usuario, categoria);

            listaService.salvarLista(novaLista);

            try {
                return HttpResponse.redirect(new URI("/listas?sucesso=lista_criada"));
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
                return HttpResponse.serverError("Error building success redirect URI.");
            }
        } catch (ConstraintViolationException e) {
            e.printStackTrace(); // Log validation errors
            try {
                return HttpResponse.redirect(new URI("/listas?erro=validacao"));
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
                return HttpResponse.serverError("Error building validation error redirect URI.");
            }
        } catch (CategoriaNaoEncontradaException e) {
            e.printStackTrace(); // Log category not found errors
            try {
                return HttpResponse.redirect(new URI("/listas?erro=categoria_nao_encontrada"));
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
                return HttpResponse.serverError("Error building category not found redirect URI.");
            }
        } catch (RuntimeException e) { // Catch the "User not found" specific error here
            e.printStackTrace(); // Log the user not found error
            try {
                return HttpResponse.redirect(new URI("/listas?erro=usuario_nao_encontrado")); // Specific error type
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
                return HttpResponse.serverError("Error building user not found redirect URI.");
            }
        }
        catch (Exception e) { // Catch any other unexpected errors
            e.printStackTrace();
            try {
                return HttpResponse.redirect(new URI("/listas?erro=inesperado"));
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
                return HttpResponse.serverError("Error building unexpected error redirect URI.");
            }
        }
    }
    // Removed PUT and DELETE methods, assuming they belong to a separate API controller.
}