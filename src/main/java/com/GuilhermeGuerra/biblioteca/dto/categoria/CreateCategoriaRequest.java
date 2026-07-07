package com.GuilhermeGuerra.biblioteca.dto.categoria;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoriaRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Descrição é obrigatória")
        String descricao
) {
}
