package com.unitins.model;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;

import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Serdeable
@MappedEntity("usuarios")
public record Usuario(
        @Id @GeneratedValue(GeneratedValue.Type.AUTO) Long id,
        @NotBlank(message = "O nome do usuário não pode estar em branco") @Size(max = 150, message = "O nome do usuário deve ter no máximo 150 caracteres") String nome) {
}