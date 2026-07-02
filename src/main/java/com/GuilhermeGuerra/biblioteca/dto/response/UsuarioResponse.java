package com.GuilhermeGuerra.biblioteca.dto.response;

import com.GuilhermeGuerra.biblioteca.entity.Usuario;

import java.time.LocalDateTime;
import java.util.Set;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String senha,
        boolean ativo,
        String telefone,
        LocalDateTime dataCadastro,
        Set<String> roles,
        String tipoPerfil
) {
    public static UsuarioResponse fromEntity(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.isAtivo(),
                usuario.getDataCadastro(),
                usuario.getRoles(),
                descobrirTipoPerfil(usuario)
        );
    }

    private descobrirTipoPerfil(Usuario usuario) {
        if(usuario.getAluno() != null) {
            return "ALUNO";
        }
        if (usuario.getProfessor() != null) {
            return "PROFESSOR";
        }
        if (usuario.getColaborador() != null) {
        return "COLABORADOR";
        }
        return "NENHUM";
        }
}
