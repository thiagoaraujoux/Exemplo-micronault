package com.unitins.repository;

import com.unitins.model.Usuario;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
}