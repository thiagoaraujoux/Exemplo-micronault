package com.unitins.dto;

import io.micronaut.core.annotation.Introspected;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) para requisições de criação e atualização de Lista.
 * Utilizado para receber os dados do cliente e validar as informações antes de processá-las.
 */
@Introspected // Necessário para Micronaut realizar a introspecção e validação
public record ListaRequestDTO(
    @NotBlank(message = "O título da lista não pode ser vazio.")
    @Size(max = 255, message = "O título da lista não pode ter mais de 255 caracteres.")
    String titulo,

    @Size(max = 1000, message = "A descrição da lista não pode ter mais de 1000 caracteres.")
    String descricao,

    // O usuarioId é incluído no DTO para que o serviço possa verificar a propriedade da lista
    // mas pode ser nulo na entrada se o controlador o preencher a partir da sessão.
    // Para requisições de API, ele virá da sessão, não do corpo da requisição.
    // Para o método criarLista, ele é preenchido pelo controlador web.
    Long usuarioId,

    // O categoriaId pode ser nulo se a lista não tiver uma categoria associada
    Long categoriaId
) {
    // Construtor adicional se necessário para manipulação interna,
    // mas para records, o construtor canônico é geralmente suficiente.
}
