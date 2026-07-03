package com.GuilhermeGuerra.biblioteca.dto.aluno;

import com.GuilhermeGuerra.biblioteca.entity.Aluno;
import com.GuilhermeGuerra.biblioteca.entity.Usuario;

public record AlunoResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        String matricula,
        String curso,
        Byte periodo,
        boolean ativo
) {
    public static AlunoResponse fromEntity(Aluno aluno) {
        return new AlunoResponse(
                aluno.getId(),
                aluno.getUsuario().getNome(),
                aluno.getUsuario().getEmail(),
                aluno.getUsuario().getTelefone(),
                aluno.getMatricula(),
                aluno.getCurso(),
                aluno.getPeriodo(),
                aluno.getUsuario().isAtivo()
        );
    }

}
