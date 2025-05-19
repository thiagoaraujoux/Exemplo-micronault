package com.unitins.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

import com.unitins.model.Emprestimo;
import com.unitins.model.Livro;
import com.unitins.model.Usuario;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface EmprestimoRepository extends CrudRepository<Emprestimo, Long> {
    // Encontra todos os empréstimos ativos para um determinado livro
    List<Emprestimo> findByLivroAndAtivoTrue(Livro livro);

    // Encontra todos os empréstimos ativos para um determinado usuário
    List<Emprestimo> findByUsuarioAndAtivoTrue(Usuario usuario);

    // Encontra todos os empréstimos (ativos ou não) para um determinado livro
    List<Emprestimo> findByLivro(Livro livro);

    // Encontra todos os empréstimos (ativos ou não) para um determinado usuário
    List<Emprestimo> findByUsuario(Usuario usuario);
}