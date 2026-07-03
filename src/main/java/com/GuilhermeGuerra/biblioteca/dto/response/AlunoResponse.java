package com.GuilhermeGuerra.biblioteca.dto.response;

import com.GuilhermeGuerra.biblioteca.entity.Aluno;
import com.GuilhermeGuerra.biblioteca.entity.Usuario;

public record AlunoResponse(
        Usuario usuario,
        String matricula,
        String curso,
        Byte periodo
) {
    public static AlunoResponse fromEntity(Aluno aluno) {
        return new AlunoResponse(
                aluno.getUsuario(),
                aluno.getMatricula(),
                aluno.getCurso(),
                aluno.getPeriodo()
        );
    }

}
