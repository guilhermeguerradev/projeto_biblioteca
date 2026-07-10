package com.GuilhermeGuerra.biblioteca.dto.professor;

import com.GuilhermeGuerra.biblioteca.entity.Professor;
import com.GuilhermeGuerra.biblioteca.entity.Usuario;

public record ProfessorResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        String siape,
        String departamento,
        String titulacao
) {
    public static ProfessorResponse fromEntity(Professor professor) {
        return new ProfessorResponse(
                professor.getId(),
                professor.getUsuario().getNome(),
                professor.getUsuario().getEmail(),
                professor.getUsuario().getTelefone(),
                professor.getSiape(),
                professor.getDepartamento(),
                professor.getTitulacao()
        );
    }
}
