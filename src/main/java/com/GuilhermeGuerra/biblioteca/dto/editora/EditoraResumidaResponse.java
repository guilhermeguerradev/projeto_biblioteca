package com.GuilhermeGuerra.biblioteca.dto.editora;

import lombok.Builder;

@Builder
public record EditoraResumidaResponse(
        Long id,
        String nome
) {
}
