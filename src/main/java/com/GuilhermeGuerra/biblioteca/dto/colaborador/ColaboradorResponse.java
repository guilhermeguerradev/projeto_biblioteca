package com.GuilhermeGuerra.biblioteca.dto.colaborador;

import com.GuilhermeGuerra.biblioteca.entity.Colaborador;
import com.GuilhermeGuerra.biblioteca.entity.enums.Cargo;
import com.GuilhermeGuerra.biblioteca.entity.enums.Setor;

public record ColaboradorResponse (
        Long id,
        String nome,
        String senha,
        String telefone,
        Cargo cargo,
        Setor setor

){
    public static ColaboradorResponse fromEntity(Colaborador colaborador) {
        return new ColaboradorResponse(
                colaborador.getId(),
                colaborador.getUsuario().getNome(),
                colaborador.getUsuario().getSenha(),
                colaborador.getUsuario().getTelefone(),
                colaborador.getCargo(),
                colaborador.getSetor()
        );
    }
}
