package com.GuilhermeGuerra.biblioteca.dto.autor;

import lombok.Builder;

@Builder
public record AutorResumidoResponse(
        Long id,
        String nome
) {
}
