package com.unitins.controller;

import com.unitins.model.Usuario;
import com.unitins.repository.UsuarioRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.views.View; // Para renderizar templates Thymeleaf
import io.micronaut.session.Session; // Para gerenciar sessões
import org.mindrot.jbcrypt.BCrypt; // Para verificar senhas hashed (necessário ter a dependência jbcrypt no pom.xml)

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller("/login") // Define o caminho base para este controlador HTTP
public class LoginController {

    private final UsuarioRepository usuarioRepository; // Injeção do repositório de usuários

    // Construtor para injeção de dependência via Micronaut
    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * GET /login
     * Exibe a página de login.
     * Este método é acessado quando o navegador solicita a URL "/login".
     *
     * @param erro Parâmetro de query opcional para indicar um erro de login (ex: "invalido").
     * @param sucesso Parâmetro de query opcional para indicar sucesso no login (ex: "true").
     * @return Um mapa de modelo que será passado para o template Thymeleaf "login.html".
     */
    @Get // Mapeia para requisições GET no caminho base do controlador (/login)
    @Produces(MediaType.TEXT_HTML) // Indica que a resposta será HTML
    @View("login") // Especifica o nome do template Thymeleaf a ser renderizado (src/main/resources/views/login.html)
    public Map<String, Object> showLogin(@QueryValue Optional<String> erro, @QueryValue Optional<String> sucesso) {
        Map<String, Object> model = new HashMap<>();
        // Os parâmetros 'erro' e 'sucesso' serão lidos diretamente pelo JavaScript no template 'login.html'
        // para exibir mensagens dinamicamente ao usuário.
        return model;
    }

    /**
     * POST /login
     * Processa as credenciais de login enviadas através do formulário HTML.
     *
     * @param form Mapa contendo os dados do formulário (chaves esperadas: "email" e "senha").
     * @param session Objeto de sessão do Micronaut para gerenciar o estado do usuário (autenticado ou não).
     * @return Um redirecionamento HTTP:
     * - Para "/listas" em caso de login bem-sucedido.
     * - Para "/login?erro=invalido" em caso de falha na autenticação.
     * - Um erro 500 se houver um problema interno ao construir a URI de redirecionamento.
     */
    @Post(consumes = MediaType.APPLICATION_FORM_URLENCODED) // Mapeia para requisições POST com dados de formulário (default de formulários HTML)
    public HttpResponse<?> login(@Body Map<String, String> form, Session session) {
        String email = form.get("email");
        String senhaFormulario = form.get("senha"); // Senha em texto plano recebida do formulário

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        if (usuarioOptional.isPresent() && BCrypt.checkpw(senhaFormulario, usuarioOptional.get().senha())) {
            // Se a autenticação for bem-sucedida:
            // Armazena o ID do usuário na sessão. Isso marca o usuário como "logado".
            session.put("usuarioId", usuarioOptional.get().id()); // Acessa o ID do record Usuario usando .id()

            // Redireciona DIRETAMENTE para a página de listas.
            try {
                URI uri = new URI("/listas"); // Redirecionamento direto para /listas
                return HttpResponse.redirect(uri);
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
                return HttpResponse.serverError("Erro ao construir URI de redirecionamento de sucesso.");
            }
        } else {
            // Se a autenticação falhar (usuário não encontrado ou senha incorreta):
            // Redireciona de volta para a página de login, adicionando um parâmetro de erro.
            try {
                URI uri = new URI("/login?erro=invalido");
                return HttpResponse.redirect(uri);
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
                return HttpResponse.serverError("Erro ao construir URI de redirecionamento de erro de login.");
            }
        }
    }

    /**
     * GET /login/logout
     * Realiza o logout do usuário, removendo o ID do usuário da sessão.
     * Este método é acessado quando o usuário clica em "Sair" ou "Logout".
     *
     * @param session Objeto de sessão do Micronaut.
     * @return Um redirecionamento HTTP para a página de login após o logout.
     */
    @Get("/logout") // Mapeia para requisições GET no caminho /login/logout
    public HttpResponse<?> logout(Session session) {
        session.remove("usuarioId"); // Remove o atributo 'usuarioId' da sessão, efetivamente deslogando o usuário
        return HttpResponse.redirect(URI.create("/login")); // Redireciona para a página de login
    }
}
