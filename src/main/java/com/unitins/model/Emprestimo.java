package com.unitins.model;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation; // Para definir relacionamentos
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Serdeable
@MappedEntity("emprestimos")
public record Emprestimo(
        @Id @GeneratedValue(GeneratedValue.Type.AUTO) Long id,
        @NotNull(message = "O ID do livro é obrigatório") @Relation(value = Relation.Kind.MANY_TO_ONE) // Muitos para um
        Livro livro, // Representa a chave estrangeira para a tabela 'livros'
        @NotNull(message = "O ID do usuário é obrigatório") @Relation(value = Relation.Kind.MANY_TO_ONE) Usuario usuario,

        @NotNull(message = "A data do empréstimo é obrigatória") LocalDate dataEmprestimo,
        @Nullable // A data de devolução pode ser nula
        LocalDate dataDevolucao,
        @NotNull(message = "O status ativo é obrigatório") Boolean ativo // empréstimo está ativo (true) ou já foi
                                                                         // devolvido (false)
) {
}