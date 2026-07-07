package com.GuilhermeGuerra.biblioteca.dto.professor;

import com.GuilhermeGuerra.biblioteca.entity.Professor;
import com.GuilhermeGuerra.biblioteca.entity.Usuario;

public record ProfessorResponse(
        Long id,
        Usuario usuario,
        String siape,
        String departamento,
        String titulacao
) {
    public static ProfessorResponse fromEntity(Professor professor) {
        return new ProfessorResponse(
                professor.getId(),
                professor.getUsuario(),
                professor.getSiape(),
                professor.getDepartamento(),
                professor.getTitulacao()
        );
    }
}
