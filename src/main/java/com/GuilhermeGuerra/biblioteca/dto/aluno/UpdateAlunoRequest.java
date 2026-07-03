package com.GuilhermeGuerra.biblioteca.dto.aluno;

import jakarta.validation.constraints.*;

public record UpdateAlunoRequest(
        String nome,

        @Email(message = "O e-mail informado é inválido")
        String email,

        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String senha,

        String telefone,

        String curso,

        @Min(value = 1, message = "O período deve ser 1 ou 2.")
        @Max(value = 2, message = "O período deve ser 1 ou 2.")
        Byte periodo
) {
}
