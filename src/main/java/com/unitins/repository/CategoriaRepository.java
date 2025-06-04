package com.unitins.repository;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

import com.unitins.model.Categoria;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface CategoriaRepository extends CrudRepository<Categoria, Long> {

    @Query("UPDATE categoria SET nome = :nome WHERE id = :id")
    int update(Long id, String nome);

    Optional<Categoria> findByNome(String nome);

    // Você pode adicionar outros métodos de busca personalizados aqui, se
    // necessário
}
