// src/main/java/com/unitins/repository/CategoriaRepository.java
package com.unitins.repository;

import com.unitins.model.Categoria;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@JdbcRepository(dialect = Dialect.MYSQL) // Importante para o Micronaut Data saber que é MySQL
public interface CategoriaRepository extends CrudRepository<Categoria, Long> {

    // Método para buscar categoria pelo nome (usado na validação de unicidade)
    Optional<Categoria> findByNome(String nome);

    // Método de atualização explícito com a query SQL para o MySQL
    // O nome da tabela "categoria" aqui DEVE corresponder ao nome real da sua tabela no banco de dados.
    @Query("UPDATE categorias SET nome = :novoNome WHERE id = :id")
    int update(Long id, String novoNome);
}