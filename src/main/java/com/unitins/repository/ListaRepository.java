package com.unitins.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.annotation.Join;
// import io.micronaut.data.annotation.Query; // Não precisamos desta importação para esta solução
import io.micronaut.data.annotation.Query;

import java.util.List;
import java.util.Optional;
import com.unitins.model.Lista;
import com.unitins.model.Usuario;
import com.unitins.model.Categoria;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface ListaRepository extends CrudRepository<Lista, Long> {

    // Método de atualização explícito para a lista (este continua correto)
    @Query("UPDATE lista SET titulo = :titulo, descricao = :descricao, categoria_id = :categoriaId WHERE id = :id")
    int update(Long id, String titulo, String descricao, Long categoriaId);

    // O @Join para findById está correto e deve funcionar
    @Join(value = "usuario", type = Join.Type.FETCH)
    @Join(value = "categoria", type = Join.Type.FETCH)
    @Override
    Optional<Lista> findById(Long id);

    // Os métodos findBy... continuam usando @Join corretamente
    @Join(value = "usuario", type = Join.Type.FETCH)
    @Join(value = "categoria", type = Join.Type.FETCH)
    List<Lista> findByUsuario(Usuario usuario);

    @Join(value = "usuario", type = Join.Type.FETCH)
    @Join(value = "categoria", type = Join.Type.FETCH)
    List<Lista> findByCategoria(Categoria categoria);

    @Join(value = "usuario", type = Join.Type.FETCH)
    @Join(value = "categoria", type = Join.Type.FETCH)
    List<Lista> findByUsuarioAndCategoria(Usuario usuario, Categoria categoria);

    @Join(value = "usuario", type = Join.Type.FETCH)
    @Join(value = "categoria", type = Join.Type.FETCH)
    List<Lista> findByUsuarioId(Long usuarioId);

    @Join(value = "usuario", type = Join.Type.FETCH)
    @Join(value = "categoria", type = Join.Type.FETCH)
    List<Lista> findByCategoriaId(Long categoriaId);
}