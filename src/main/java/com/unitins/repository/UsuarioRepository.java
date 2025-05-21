package com.unitins.repository;

import com.unitins.model.Usuario;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional; // Importa Optional para métodos que podem não encontrar um resultado

@JdbcRepository(dialect = Dialect.MYSQL) // Indica que este é um repositório JDBC para MySQL
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo seu endereço de e-mail.
     * Retorna um Optional pois o e-mail é único, mas pode não haver nenhum usuário com o e-mail especificado.
     * Micronaut Data implementa este método automaticamente baseado no nome.
     * @param email O endereço de e-mail do usuário a ser buscado.
     * @return Um Optional contendo o Usuario se encontrado, ou um Optional vazio caso contrário.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se um usuário com o endereço de e-mail especificado existe.
     * Útil para validações antes de criar um novo usuário, por exemplo.
     * Micronaut Data implementa este método automaticamente baseado no nome.
     * @param email O endereço de e-mail a ser verificado.
     * @return true se um usuário com o e-mail existir, false caso contrário.
     */
    boolean existsByEmail(String email);

    // O CrudRepository já fornece métodos básicos como findById(Long id), findAll(), save(Usuario usuario), delete(Usuario usuario), etc.
    // Você pode adicionar outros métodos de busca personalizados aqui conforme necessário.
    // Exemplo: buscar usuários cujo nome contenha uma determinada string (pode não ser ideal para todos os casos)
    // List<Usuario> findByNomeContains(String nome);
}
