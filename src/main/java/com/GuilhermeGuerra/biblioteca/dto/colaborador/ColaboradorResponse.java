package com.GuilhermeGuerra.biblioteca.dto.colaborador;

import com.GuilhermeGuerra.biblioteca.entity.Colaborador;
import com.GuilhermeGuerra.biblioteca.entity.enums.Cargo;
import com.GuilhermeGuerra.biblioteca.entity.enums.Setor;

import java.time.LocalDate;

public record ColaboradorResponse (
        Long id,
        String nome,
        String email,
        String telefone,
        Cargo cargo,
        Setor setor,
        LocalDate dataAdmissao

){
    public static ColaboradorResponse fromEntity(Colaborador colaborador) {
        return new ColaboradorResponse(
                colaborador.getId(),
                colaborador.getUsuario().getNome(),
                colaborador.getUsuario().getEmail(),
                colaborador.getUsuario().getTelefone(),
                colaborador.getCargo(),
                colaborador.getSetor(),
                colaborador.getDataAdmissao()
        );
    }
}
