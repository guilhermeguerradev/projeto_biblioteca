package com.GuilhermeGuerra.biblioteca.dto.request.autor;

import jakarta.validation.constraints.NotBlank;

public record CreateAutorRequest(
        @NotBlank(message = "Nome é obrigatório")
        String nome,
        @NotBlank(message = "Nacionalidade é obrigatória")
        String nacionalidade
) {
}
