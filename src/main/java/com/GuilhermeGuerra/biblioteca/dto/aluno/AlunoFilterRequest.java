package com.GuilhermeGuerra.biblioteca.dto.aluno;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record AlunoFilterRequest(
        String nome,
        String matricula,
        String curso,
        Byte periodo,
        Boolean ativo,

        Integer page,
        Integer size,
        String sort,
        String direction
) {

    public Pageable toPageable() {
        int paginaAtual = (this.page == null || this.page < 0) ? 0 : this.page;

        int tamanhoPagina = (this.size == null || this.size < 1 || this.size > 100) ? 20 : this.size;

        String atributoOrdenacao = (this.sort == null || this.sort.isBlank()) ? "usuario.nome" : this.sort;

        Sort.Direction direcao = "asc".equalsIgnoreCase(this.direction) ? Sort.Direction.ASC : Sort.Direction.DESC;

        return PageRequest.of(paginaAtual,tamanhoPagina, Sort.by(direcao,atributoOrdenacao));
    }
}
