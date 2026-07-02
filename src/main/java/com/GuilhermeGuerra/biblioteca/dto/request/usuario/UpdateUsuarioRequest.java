package com.GuilhermeGuerra.biblioteca.dto.request.usuario;


import com.GuilhermeGuerra.biblioteca.entity.enums.TipoRole;

import java.util.Set;

public record UpdateUsuarioRequest(
        String nome,
        String email,
        String senha,
        String telefone,
        Set<TipoRole> roles
) {
}
