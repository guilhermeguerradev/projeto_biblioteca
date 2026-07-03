package com.GuilhermeGuerra.biblioteca.dto.colaborador;

import com.GuilhermeGuerra.biblioteca.dto.util.PaginavelRequest;
import com.GuilhermeGuerra.biblioteca.entity.enums.Cargo;
import com.GuilhermeGuerra.biblioteca.entity.enums.Setor;

public record FilterColaboradorRequest (

        String nome,
        Cargo cargo,
        Setor setor,
        Boolean ativo,


        Integer page,
        Integer size,
        String sort,
        String direction
) implements PaginavelRequest {
}
