package com.GuilhermeGuerra.biblioteca.dto.aluno;

import jakarta.validation.constraints.*;

public record CreateAlunoRequest(
    @NotBlank(message = "O nome é obrigatório")
    String nome,

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "O e-mail informado é inválido")
    String email,

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    String senha,

    String telefone,

    @NotBlank(message = "O nome é obrigatório")
    String curso,

    @NotNull(message = "O período é obrigatório.")
    @Min(value = 1, message = "O período deve ser 1 ou 2.")
    @Max(value = 2, message = "O período deve ser 1 ou 2.")
    Byte periodo
) {
}
