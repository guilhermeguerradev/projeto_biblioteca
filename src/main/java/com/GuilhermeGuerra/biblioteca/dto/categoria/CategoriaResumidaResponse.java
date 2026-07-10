package com.GuilhermeGuerra.biblioteca.dto.categoria;

import lombok.Builder;

@Builder
public record CategoriaResumidaResponse(
        Long id,
        String nome
) {
}
