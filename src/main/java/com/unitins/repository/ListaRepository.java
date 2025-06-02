package com.unitins.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.annotation.Join;

import java.util.List;
import java.util.Optional;
import com.unitins.model.Lista;
import com.unitins.model.Usuario;
import com.unitins.model.Categoria;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface ListaRepository extends CrudRepository<Lista, Long> {

    /**
     * Busca uma lista pelo seu ID, carregando o usuário e a categoria associados.
     * Isso é essencial para evitar que 'usuario' e 'categoria' sejam nulos ao serem recuperados.
     *
     * @param id O ID da lista a ser buscada.
     * @return Um Optional contendo a Lista se encontrada, ou um Optional vazio caso contrário.
     */
    @Join(value = "usuario", type = Join.Type.FETCH)
    @Join(value = "categoria", type = Join.Type.FETCH)
    @Override
    Optional<Lista> findById(Long id);


    /**
     * Busca todas as listas pertencentes a um usuário específico.
     * Micronaut Data implementa este método automaticamente baseado no nome.
     * @param usuario O objeto Usuario pelo qual buscar as listas.
     * @return Uma lista de Listas associadas ao usuário.
     */
    List<Lista> findByUsuario(Usuario usuario);

    /**
     * Busca todas as listas associadas a uma categoria específica.
     * Micronaut Data implementa este método automaticamente baseado no nome.
     * @param categoria O objeto Categoria pelo qual buscar as listas.
     * @return Uma lista de Listas associadas à categoria.
     */
    List<Lista> findByCategoria(Categoria categoria);

    /**
     * Busca todas as listas pertencentes a um usuário específico e associadas a uma categoria específica.
     * Micronaut Data implementa este método automaticamente baseado no nome.
     * @param usuario O objeto Usuario pelo qual buscar.
     * @param categoria O objeto Categoria pelo qual buscar.
     * @return Uma lista de Listas que correspondem ao usuário e categoria.
     */
    List<Lista> findByUsuarioAndCategoria(Usuario usuario, Categoria categoria);

    /**
     * Busca todas as listas pertencentes a um usuário específico pelo ID do usuário.
     * Útil quando você tem apenas o ID do usuário e não o objeto completo.
     * @param usuarioId O ID do usuário pelo qual buscar as listas.
     * @return Uma lista de Listas associadas ao ID do usuário.
     */
    List<Lista> findByUsuarioId(Long usuarioId);

    /**
     * Busca todas as listas associadas a uma categoria específica pelo ID da categoria.
     * Útil quando você tem apenas o ID da categoria e não o objeto completo.
     * @param categoriaId O ID da categoria pelo qual buscar as listas.
     * @return Uma lista de Listas associadas ao ID da categoria.
     */
    List<Lista> findByCategoriaId(Long categoriaId);
}
