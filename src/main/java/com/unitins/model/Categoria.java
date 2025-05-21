package com.unitins.model;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Serdeable // Anotação para serialização/desserialização (JSON, etc.)
@MappedEntity("categorias") // Mapeia este record para a tabela 'categorias'
@Schema(description = "Representa uma categoria para listas") // Documentação Swagger/OpenAPI
public record Categoria(
    @Id // Indica que este campo é a chave primária
    @GeneratedValue(GeneratedValue.Type.AUTO) // Configura a geração automática do ID
    @Schema(description = "Identificador único da categoria", example = "1")
    Long id,

    @NotNull // Validação: o nome não pode ser nulo
    @Size(min = 1, max = 100) // Validação: tamanho do nome
    @Schema(description = "Nome da categoria", example = "Trabalho")
    String nome
) {
    // Records automaticamente geram construtor, métodos de acesso (id(), nome()), equals(), hashCode(), toString()
    // Você pode adicionar métodos de instância ou estáticos aqui se precisar de lógica específica
}
