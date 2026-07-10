package com.GuilhermeGuerra.biblioteca.dto.usuario;

import lombok.Builder;

@Builder
public record UpdateUsuarioRequest(
        String nome,
        String email,
        String senha,
        String telefone
) {
}
