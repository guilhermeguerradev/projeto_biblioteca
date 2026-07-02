package com.GuilhermeGuerra.biblioteca.dto.request.professor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateProfessorRequest(
        String nome,
        @Email(message = "O e-mail informado é inválido")
        String email,
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String senha,
        String telefone,
        String siape,
        String departamento,
        String titulacao
) {
}
