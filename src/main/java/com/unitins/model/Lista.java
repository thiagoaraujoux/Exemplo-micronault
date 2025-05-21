package com.unitins.model;

import io.micronaut.data.annotation.*;
import io.micronaut.data.annotation.sql.JoinColumn;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size; // Importa Size para validação de tamanho
import java.time.LocalDate; // Importa LocalDate para o tipo DATE

@Serdeable // Anotação para serialização/desserialização (JSON, etc.)
@MappedEntity("lista") // Mapeia este record para a tabela 'lista' (nome da tabela no SQL)
@Schema(description = "Representa uma lista do sistema") // Documentação Swagger/OpenAPI
public record Lista(
    @Id // Indica que este campo é a chave primária
    @GeneratedValue(GeneratedValue.Type.AUTO) // Configura a geração automática do ID
    @Schema(description = "Identificador único da lista", example = "1")
    Long id,

    @NotNull // Validação: o título não pode ser nulo
    @Size(min = 1, max = 150) // Validação: tamanho do título
    @Schema(description = "Título da lista", example = "Minha Lista de Compras")
    String titulo, // Alterado de 'nome' para 'titulo'

    @Schema(description = "Descrição detalhada da lista")
    String descricao, // Adicionado campo 'descricao' (TEXT no SQL, String em Java)

    @NotNull // Validação: a data de criação não pode ser nula
    @Schema(description = "Data de criação da lista", example = "2023-10-26")
    LocalDate dataCriacao, // Adicionado campo 'data_criacao' (DATE no SQL, LocalDate em Java)

    @NotNull // Validação: o usuário não pode ser nulo
    @Relation(Relation.Kind.MANY_TO_ONE) // Define um relacionamento muitos-para-um
    @JoinColumn(name = "usuario_id") // Mapeia para a coluna 'usuario_id' no banco de dados
    @Schema(description = "Usuário dono da lista")
    Usuario usuario,

    // Removido @NotNull aqui, pois categoria_id é BIGINT (nullable) no SQL
    @Relation(Relation.Kind.MANY_TO_ONE) // Define um relacionamento muitos-para-um
    @JoinColumn(name = "categoria_id") // Mapeia para a coluna 'categoria_id' no banco de dados
    @Schema(description = "Categoria associada à lista (opcional)")
    Categoria categoria // Categoria pode ser nula
) {
    // Records automaticamente geram construtor, métodos de acesso (id(), titulo(), etc.), equals(), hashCode(), toString()
    // Você pode adicionar métodos de instância ou estáticos aqui se precisar de lógica específica
}
