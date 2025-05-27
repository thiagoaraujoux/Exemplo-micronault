// src/main/java/com/unitins/model/ApiResponseMessage.java
package com.unitins.model;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

@Serdeable
@Schema(description = "Estrutura para mensagens de resposta da API")
public record ApiResponseMessage(
    @Schema(description = "Mensagem descritiva da operação", example = "Operação realizada com sucesso.")
    String message,

    @Schema(description = "Indica se a operação foi bem-sucedida", example = "true")
    boolean success
) {
    public static ApiResponseMessage success(String message) {
        return new ApiResponseMessage(message, true);
    }

    public static ApiResponseMessage error(String message) {
        return new ApiResponseMessage(message, false);
    }
}