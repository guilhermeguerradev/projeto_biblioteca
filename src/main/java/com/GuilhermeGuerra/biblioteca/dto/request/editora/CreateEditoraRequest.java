package com.GuilhermeGuerra.biblioteca.dto.request.editora;

import jakarta.validation.constraints.NotBlank;

public record CreateEditoraRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome,
        @NotBlank(message = "Nome é obrigatório")
        String cidade,
        @NotBlank(message = "Nome é obrigatório")
        String estado,
        @NotBlank(message = "Nome é obrigatório")
        String pais

) {
}
