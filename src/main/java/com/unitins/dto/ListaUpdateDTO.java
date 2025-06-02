package com.unitins.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable; // Importe esta anotação!
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;

@Introspected
@Serdeable.Deserializable // Adicione esta anotação para desserialização
// Opcional: Se este DTO também for retornado em respostas, você pode usar @Serdeable para ambos
// @Serdeable
public class ListaUpdateDTO {

    @Size(max = 255, message = "O título não pode ter mais de 255 caracteres.")
    private String titulo;

    @Nullable // Indica que este campo pode ser nulo
    @Size(max = 1000, message = "A descrição não pode ter mais de 1000 caracteres.")
    private String descricao;

    @Nullable // Isso é crucial: categoriaId pode ser nulo se nenhuma categoria for selecionada
    private Long categoriaId;

    // Getters
    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    // Setters (necessários para desserialização pelo binder padrão do Micronaut)
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }
}
