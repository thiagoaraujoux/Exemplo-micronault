package com.unitins.service.impl; // Pacote de implementação

import com.unitins.model.Usuario;
import com.unitins.repository.UsuarioRepository;
import com.unitins.service.UsuarioService; // Importa a interface do serviço
import com.unitins.service.exception.UsuarioNaoEncontradoException;

import jakarta.inject.Singleton; // Anotação para indicar que é um bean singleton
import io.micronaut.transaction.annotation.Transactional; // Anotação de transação do Micronaut

import java.util.Optional;

@Singleton // Micronaut gerencia esta classe como um singleton
public class UsuarioServiceImpl implements UsuarioService { // Implementa a interface do serviço

    private final UsuarioRepository usuarioRepository; // Injeta o repositório
    // Você pode precisar injetar um serviço de criptografia de senha aqui, se aplicável

    // Construtor para injeção de dependência
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional // Marca o método como transacional (escrita)
    public Usuario criarUsuario(Usuario usuario) {
        // Lógica adicional de negócio antes de salvar, se necessário
        // Ex: Criptografar a senha antes de salvar
        // usuario = new Usuario(null, usuario.nome(), usuario.email(), criptografarSenha(usuario.senha()));
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true) // Marca o método como transacional (somente leitura)
    public Iterable<Usuario> buscarTodos() {
        return usuarioRepository.findAll(); // Busca todos os usuários
    }

    @Override
    @Transactional(readOnly = true) // Marca o método como transacional (somente leitura)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id); // Busca um usuário pelo ID
    }

    @Override
    @Transactional // Marca o método como transacional (escrita)
    public Usuario salvarUsuario(Usuario usuario) {
        // Implementação para salvar/criar um usuário
        // Ex: Criptografar a senha antes de salvar
        // usuario = new Usuario(null, usuario.nome(), usuario.email(), criptografarSenha(usuario.senha()));
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional // Marca o método como transacional (escrita)
    public void deletarUsuario(Long id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            usuarioRepository.delete(usuarioOptional.get()); // Deleta o usuário encontrado
        } else {
            // Lança exceção se o usuário não for encontrado
            throw new UsuarioNaoEncontradoException("Usuário com ID " + id + " não encontrado"); // <--- Used here
        }
    }

    @Override
    @Transactional // Marca o método como transacional (escrita)
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuarioExistente = usuarioOptional.get();
            // Records são imutáveis, então criamos um novo record com os dados atualizados.
            // Mantemos o ID existente e atualizamos nome, email e senha.
            Usuario usuarioParaSalvar = new Usuario(
                usuarioExistente.id(), // Mantém o ID existente
                usuarioAtualizado.nome(), // Usa o nome do record de entrada
                usuarioAtualizado.email(), // Usa o email do record de entrada
                // Cuidado ao atualizar a senha aqui. Você provavelmente não quer substituir uma senha hashed
                // por uma senha em texto plano. Implemente a lógica de atualização de senha separadamente
                usuarioAtualizado.senha() // Usa a senha do record de entrada (pode precisar de criptografia)
            );

            // O método save() do CrudRepository para records com @Id e @GeneratedValue
            // geralmente lida com a atualização se o ID já existir.
            return usuarioRepository.save(usuarioParaSalvar);
        } else {
            // Lança exceção se o usuário não for encontrado
            throw new UsuarioNaoEncontradoException("Usuário com ID " + id + " não encontrado"); // <--- Used here
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email); // Busca usuário por email
    }

    // Método auxiliar para criptografar senha (exemplo - implemente a lógica real)
    // private String criptografarSenha(String senha) {
    //     // Implementação real de criptografia (ex: BCrypt)
    //     return senha; // Placeholder
    // }
}
