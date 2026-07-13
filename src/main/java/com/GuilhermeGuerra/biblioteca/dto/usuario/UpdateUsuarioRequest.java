package com.GuilhermeGuerra.biblioteca.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateUsuarioRequest(
        String nome,
        @Email(message = "O e-mail informado é inválido")
        String email,
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String senha,
        String telefone
) {
}
