package com.unitins.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import com.unitins.model.Lista;
import com.unitins.model.Usuario;
import com.unitins.model.Categoria;

@JdbcRepository(dialect = Dialect.MYSQL) // Indica que este é um repositório JDBC para MySQL
public interface ListaRepository extends CrudRepository<Lista, Long> {

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

    /**
     * Busca uma lista pelo seu título.
     * Retorna um Optional pois pode não haver nenhuma lista com o título especificado.
     * @param titulo O título da lista a ser buscada.
     * @return Um Optional contendo a Lista se encontrada, ou um Optional vazio caso contrário.
     */
    

    // Você pode adicionar outros métodos de busca ou paginação aqui conforme necessário.
    // Exemplo de busca paginada por usuário:
    // Page<Lista> findByUsuario(Usuario usuario, Pageable pageable);
}
