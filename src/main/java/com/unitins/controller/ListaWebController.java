package com.unitins.controller;

import com.unitins.model.Lista;
import com.unitins.model.Usuario;
import com.unitins.service.ListaService;
import com.unitins.service.UsuarioService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.views.View;
import io.micronaut.session.Session;
import jakarta.validation.ConstraintViolationException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate; // Corrected import for LocalDate
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller("/listas") // Define o caminho base para este controlador web
public class ListaWebController {

    private final ListaService listaService;
    private final UsuarioService usuarioService; // Para buscar o objeto Usuario pelo ID da sessão

    // Construtor para injeção de dependência
    public ListaWebController(ListaService listaService, UsuarioService usuarioService) {
        this.listaService = listaService;
        this.usuarioService = usuarioService;
    }

    /**
     * GET /listas
     * Exibe a página principal de listas, mostrando todas as listas do usuário logado.
     *
     * @param session A sessão atual para obter o ID do usuário logado.
     * @param erro Parâmetro de query opcional para exibir mensagens de erro.
     * @param sucesso Parâmetro de query opcional para exibir mensagens de sucesso.
     * @return Um HttpResponse contendo o modelo para o template Thymeleaf 'lista.html' ou um redirecionamento.
     */
    @Get // Mapeia para requisições GET no caminho /listas
    @Produces(MediaType.TEXT_HTML)
    @View("lista") // Renderiza o template src/main/resources/views/lista.html
    public HttpResponse<?> showListas(Session session, // Changed return type to HttpResponse<?>
                                                        @QueryValue Optional<String> erro,
                                                        @QueryValue Optional<String> sucesso) {
        Map<String, Object> model = new HashMap<>();

        // Verifica se o usuário está logado
        Long usuarioId = session.get("usuarioId", Long.class).orElse(null);

        if (usuarioId == null) {
            // Se não houver usuário na sessão, redireciona para a página de login
            try {
                return HttpResponse.redirect(new URI("/login"));
            } catch (URISyntaxException e) {
                return HttpResponse.serverError("Erro interno ao redirecionar para login.");
            }
        }

        // Adiciona as listas do usuário logado ao modelo
        // Nota: O método listarPorUsuario retorna Iterable, que é compatível com Thymeleaf.
        model.put("listas", listaService.listarPorUsuario(usuarioId));

        // Os parâmetros 'erro' e 'sucesso' serão lidos pelo JavaScript no template.
        // Não é necessário adicioná-los diretamente ao modelo, a menos que haja lógica complexa no Thymeleaf.

        return HttpResponse.ok(model);
    }

    /**
     * POST /listas
     * Processa a criação de uma nova lista a partir do formulário HTML.
     *
     * @param form Mapa contendo os dados do formulário (titulo, descricao).
     * @param session A sessão atual para obter o ID do usuário logado.
     * @return Um redirecionamento HTTP para a página de listas com parâmetro de sucesso/erro.
     */
    @Post(consumes = MediaType.APPLICATION_FORM_URLENCODED) // Recebe dados do formulário HTML
    public HttpResponse<?> criarLista(@Body Map<String, String> form, Session session) {
        String titulo = form.get("titulo");
        String descricao = form.get("descricao");

        Long usuarioId = session.get("usuarioId", Long.class).orElse(null);

        if (usuarioId == null) {
            // Se não houver usuário na sessão, redireciona para a página de login
            try {
                return HttpResponse.redirect(new URI("/login?erro=nao_logado"));
            } catch (URISyntaxException e) {
                return HttpResponse.serverError("Erro interno ao redirecionar para login.");
            }
        }

        try {
            // Busca o objeto Usuario completo para associar à lista
            Usuario usuario = usuarioService.buscarPorId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado na sessão.")); // Deu erro se chegar aqui, usuário logado não existe mais no DB

            // Cria uma nova instância de Lista (o campo categoria é null por padrão aqui, para simplificar)
            // Se Categoria for obrigatória no seu model, você precisará adaptar esta parte.
            // Corrected constructor call: Instant.now() changed to LocalDate.now()
            Lista novaLista = new Lista(null, titulo, descricao, LocalDate.now(), usuario, null); // Categoria é null por padrão

            listaService.salvarLista(novaLista);

            // Redireciona com mensagem de sucesso
            try {
                return HttpResponse.redirect(new URI("/listas?sucesso=true"));
            } catch (URISyntaxException ex) {
                return HttpResponse.serverError("Erro ao construir URI de redirecionamento de sucesso.");
            }
        } catch (ConstraintViolationException e) {
            // Captura erros de validação (se o título ou descrição tiverem restrições, por exemplo)
            try {
                return HttpResponse.redirect(new URI("/listas?erro=validacao"));
            } catch (URISyntaxException ex) {
                return HttpResponse.serverError("Erro ao construir URI de redirecionamento de erro de validação.");
            }
        } catch (Exception e) {
            // Captura outros erros inesperados
            e.printStackTrace();
            try {
                return HttpResponse.redirect(new URI("/listas?erro=inesperado"));
            } catch (URISyntaxException ex) {
                return HttpResponse.serverError("Erro ao construir URI de redirecionamento de erro inesperado.");
            }
        }
    }
}
