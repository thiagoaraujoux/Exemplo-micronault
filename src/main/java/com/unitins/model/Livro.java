package com.unitins.model;

import io.micronaut.data.annotation.*;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Serdeable
@MappedEntity("livros")
@Schema(description = "Representa um livro no sistema")
public record Livro(
    @Id
    @GeneratedValue(GeneratedValue.Type.AUTO)
    @Schema(description = "Identificador único do livro", example = "1")
    Long id,

    @NotBlank(message = "O título não pode estar em branco")
    @Size(max = 255, message = "O título deve ter no máximo 255 caracteres")
    @Schema(description = "Título do livro", example = "Dom Casmurro")
    String titulo,

    @NotBlank(message = "O autor não pode estar em branco")
    @Size(max = 100, message = "O autor deve ter no máximo 100 caracteres")
    @Schema(description = "Autor do livro", example = "Machado de Assis")
    String autor,

    @NotBlank(message = "O ISBN não pode estar em branco")
    @Size(min = 10, max = 13, message = "O ISBN deve ter entre 10 e 13 caracteres")
    @Schema(description = "ISBN do livro", example = "9781234567890")
    String isbn
) {}
