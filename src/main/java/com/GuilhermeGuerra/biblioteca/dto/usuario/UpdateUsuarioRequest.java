package com.GuilhermeGuerra.biblioteca.dto.usuario;



public record UpdateUsuarioRequest(
        String nome,
        String email,
        String senha,
        String telefone
) {
}
