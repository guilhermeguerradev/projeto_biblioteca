package com.GuilhermeGuerra.biblioteca.dto.request.usuario;



public record UpdateUsuarioRequest(
        String nome,
        String email,
        String senha,
        String telefone
) {
}
