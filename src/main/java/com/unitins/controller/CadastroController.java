package com.unitins.controller;

import com.unitins.model.Usuario;
import com.unitins.service.UsuarioService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.views.View; // Para renderizar templates Thymeleaf
import io.micronaut.http.annotation.Body; // Para receber o corpo da requisição
import io.micronaut.http.annotation.QueryValue; // Para parâmetros de query na URL
import io.micronaut.validation.Validated; // Para habilitar validação
import jakarta.validation.ConstraintViolationException; // Para capturar exceções de validação

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller("/cadastro") // Define o caminho base para este controlador
@Validated // Habilita a validação para este controlador
public class CadastroController {

    private final UsuarioService usuarioService;

    // Construtor para injeção de dependência do UsuarioService
    public CadastroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * GET /cadastro
     * Exibe a página de cadastro.
     *
     * @param erro Parâmetro de query opcional para indicar um erro (ex: "email_existente", "validacao").
     * @param sucesso Parâmetro de query opcional para indicar sucesso no cadastro.
     * @return Um mapa de modelo para o template Thymeleaf.
     */
    @Get // Mapeia para requisições GET no caminho /cadastro
    @Produces(MediaType.TEXT_HTML) // Indica que a resposta é HTML
    @View("cadastro") // Especifica o template Thymeleaf a ser renderizado (src/main/resources/views/cadastro.html)
    public Map<String, Object> showCadastro(@QueryValue Optional<String> erro, @QueryValue Optional<String> sucesso) {
        Map<String, Object> model = new HashMap<>();
        // Assim como no login, o JavaScript no template 'cadastro.html'
        // cuidará da lógica de exibição das mensagens.
        return model;
    }

    /**
     * POST /cadastro
     * Processa os dados de cadastro enviados via formulário HTML.
     *
     * @param form Mapa contendo os dados do formulário (nome, email, senha).
     * @return Um redirecionamento HTTP para a página de cadastro com parâmetros de sucesso/erro.
     */
    @Post(consumes = MediaType.APPLICATION_FORM_URLENCODED) // Mapeia para requisições POST com dados de formulário
    public HttpResponse<?> cadastrar(@Body Map<String, String> form) {
        String nome = form.get("nome");
        String email = form.get("email");
        String senha = form.get("senha");

        // Verifica se o e-mail já está em uso antes de tentar criar o usuário
        if (usuarioService.buscarPorEmail(email).isPresent()) {
            try {
                URI uri = new URI("/cadastro?erro=email_existente");
                return HttpResponse.redirect(uri);
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
                return HttpResponse.serverError("Erro ao construir URI de redirecionamento de erro de cadastro.");
            }
        }

        try {
            // Cria um novo record Usuario (o UsuarioService já fará o hashing da senha)
            Usuario novoUsuario = new Usuario(null, nome, email, senha);
            usuarioService.salvarUsuario(novoUsuario); // Chame salvarUsuario para garantir o hashing

            // Se o cadastro for bem-sucedido, redireciona com sucesso=true
            try {
                URI uri = new URI("/cadastro?sucesso=true");
                return HttpResponse.redirect(uri);
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
                return HttpResponse.serverError("Erro ao construir URI de redirecionamento de sucesso.");
            }
        } catch (ConstraintViolationException e) {
            // Captura erros de validação (ex: email inválido, senha fraca se configurado)
            // Micronaut Validation usa ConstraintViolationException para falhas de @Valid
            try {
                URI uri = new URI("/cadastro?erro=validacao");
                return HttpResponse.redirect(uri);
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
                return HttpResponse.serverError("Erro ao construir URI de redirecionamento de erro de validação.");
            }
        } catch (Exception e) {
            // Captura qualquer outro erro inesperado durante o cadastro
            e.printStackTrace();
            try {
                URI uri = new URI("/cadastro?erro=inesperado");
                return HttpResponse.redirect(uri);
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
                return HttpResponse.serverError("Erro ao construir URI de redirecionamento de erro inesperado.");
            }
        }
    }
}
