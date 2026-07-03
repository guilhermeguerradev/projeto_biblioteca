package com.GuilhermeGuerra.biblioteca.dto.colaborador;

import com.GuilhermeGuerra.biblioteca.entity.enums.Cargo;
import com.GuilhermeGuerra.biblioteca.entity.enums.Setor;
import jakarta.validation.constraints.*;

public record CreateColaboradorRequest(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail informado é inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String senha,

        String telefone,

        @NotNull(message = "O cargo é obrigatório")
        Cargo cargo,
        @NotNull(message = "O setor é obrigatório")
        Setor setor


) {
}
