package com.GuilhermeGuerra.biblioteca.dto.colaborador;

import com.GuilhermeGuerra.biblioteca.entity.enums.Cargo;
import com.GuilhermeGuerra.biblioteca.entity.enums.Setor;

public record UpdateColaboradorRequest (
        String nome,
        String email,
        String senha,
        String telefone,
        Cargo cargo,
        Setor setor
){ }
