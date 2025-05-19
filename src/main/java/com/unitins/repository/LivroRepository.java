package com.unitins.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;

import com.unitins.model.Livro;

@JdbcRepository(dialect = Dialect.MYSQL) // Especifica o dialeto do banco
public interface LivroRepository extends CrudRepository<Livro, Long> {
    // Método de query customizado para buscar um livro pelo ISBN
    // Micronaut Data infere a query: SELECT * FROM livros WHERE isbn =?
    Optional<Livro> findByIsbn(String isbn);

    // Método de query customizado para verificar a existência de um livro
    // Micronaut Data infere a query:
    // SELECT CASE WHEN COUNT(*) > 0 THEN TRUE
    // ELSE FALSE END FROM livros WHERE isbn =?
    boolean existsByIsbn(String isbn);
}