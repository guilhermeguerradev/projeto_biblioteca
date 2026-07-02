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
        LocalDateTime dataCadastro
) {
    public static UsuarioResponse fromEntity(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getSenha(),
                usuario.getEmail(),
                usuario.isAtivo(),
                usuario.getTelefone(),
                usuario.getDataCadastro()
        );
    }


}
