package com.GuilhermeGuerra.biblioteca.dto.aluno;

import com.GuilhermeGuerra.biblioteca.dto.util.PaginavelRequest;


public record FilterAlunoRequest(
        String nome,
        String matricula,
        String curso,
        Byte periodo,
        Boolean ativo,

        Integer page,
        Integer size,
        String sort,
        String direction
) implements PaginavelRequest {
}
