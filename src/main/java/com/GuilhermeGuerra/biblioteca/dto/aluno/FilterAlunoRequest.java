package com.GuilhermeGuerra.biblioteca.dto.aluno;

public record FilterAlunoRequest(
        String nome,
        String matricula,
        String curso,
        Byte periodo,
        Boolean ativo
) {
}
