package com.unitins.model;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email; // Para validação de formato de e-mail
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Serdeable // Anotação para serialização/desserialização (JSON, etc.)
@MappedEntity("usuarios") // Mapeia este record para a tabela 'usuarios'
@Schema(description = "Representa um usuário do sistema") // Documentação Swagger/OpenAPI
public record Usuario(
    @Id // Indica que este campo é a chave primária
    @GeneratedValue(GeneratedValue.Type.AUTO) // Configura a geração automática do ID
    @Schema(description = "Identificador único do usuário", example = "1")
    Long id,

    @NotNull // Validação: o nome não pode ser nulo
    @Size(min = 1, max = 150) // Validação: tamanho do nome
    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    String nome,

    @NotNull // Validação: o e-mail não pode ser nulo
    @Size(min = 5, max = 150) // Validação: tamanho do e-mail
    @Email // Validação: verifica se o formato é de e-mail válido
    @Schema(description = "Endereço de e-mail do usuário (único)", example = "joao.silva@example.com")
    String email, // Adicionado campo email

    @NotNull // Validação: a senha não pode ser nula
    @Size(min = 6, max = 100) // Validação: tamanho mínimo e máximo da senha (ajuste conforme sua política de segurança)
    @Schema(description = "Senha do usuário (hashed, não em texto plano!)")
    String senha // Adicionado campo senha
) {
    // Records automaticamente geram construtor, métodos de acesso (id(), nome(), email(), senha()), equals(), hashCode(), toString()
    // Você pode adicionar métodos de instância ou estáticos aqui se precisar de lógica específica
}
