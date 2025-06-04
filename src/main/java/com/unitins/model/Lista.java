package com.unitins.model;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.sql.JoinColumn; // Importe JoinColumn
import io.micronaut.core.annotation.Nullable; // Importe esta anotação para campos que podem ser nulos
import io.micronaut.serde.annotation.Serdeable; // Importe Serdeable para serialização/desserialização
import io.swagger.v3.oas.annotations.media.Schema; // Importe Schema para documentação Swagger/OpenAPI
import jakarta.validation.constraints.NotNull; // Importe NotNull para validação
import jakarta.validation.constraints.Size; // Importe Size para validação de tamanho

import java.time.LocalDate; // Para dataCriacao

@Serdeable // Anotação para serialização/desserialização (JSON, etc.)
@MappedEntity("lista") // Mapeia este record para a tabela 'lista' (nome da tabela no SQL)
@Schema(description = "Representa uma lista do sistema") // Documentação Swagger/OpenAPI
public record Lista(
    @Id // Indica que este campo é a chave primária
    @GeneratedValue(GeneratedValue.Type.IDENTITY) // Configura a geração automática do ID
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

    // Removido @NotNull aqui para evitar o erro "Null argument specified for [usuario]"
    // Adicionado @Nullable para permitir que o usuário seja nulo se o banco de dados permitir
    @Relation(Relation.Kind.MANY_TO_ONE) // Define um relacionamento muitos-para-um
    @JoinColumn(name = "usuario_id") // Mapeia para a coluna 'usuario_id' no banco de dados
    @Nullable // <--- MANTIDO COMO @Nullable para evitar o erro anterior
    @Schema(description = "Usuário dono da lista")
    Usuario usuario,

    // Removido @NotNull aqui, pois categoria_id é BIGINT (nullable) no SQL
    @Relation(Relation.Kind.MANY_TO_ONE) // Define um relacionamento muitos-para-um
    @JoinColumn(name = "categoria_id") // Mapeia para a coluna 'categoria_id' no banco de dados
    @Nullable // Categoria pode ser nula
    @Schema(description = "Categoria associada à lista (opcional)")
    Categoria categoria // Categoria pode ser nula
) {
    // Records automaticamente geram construtor, métodos de acesso (id(), titulo(), etc.), equals(), hashCode(), toString()
    // Você pode adicionar métodos de instância ou estáticos aqui se precisar de lógica específica
}
