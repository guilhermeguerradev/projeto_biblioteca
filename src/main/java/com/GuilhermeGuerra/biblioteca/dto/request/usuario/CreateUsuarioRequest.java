package com.GuilhermeGuerra.biblioteca.dto.request.usuario;

import com.GuilhermeGuerra.biblioteca.entity.enums.TipoRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateUsuarioRequest(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail informado é inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String senha,

        String telefone,

        @NotEmpty(message = "O usuário deve possuir pelo menos uma permissão (role)")
        Set<TipoRole> roles
) {
}
