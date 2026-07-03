package com.GuilhermeGuerra.biblioteca.dto.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


public interface PaginavelRequest {

    Integer page();
    Integer size();
    String sort();
    String direction();

    default Pageable toPageable(String ordenacaoPadrao) {
        int paginaAtual = (page() == null || page() < 0) ? 0 : page();
        int tamanhoPagina = (size() == null || size() < 1 || size() > 100) ? 20 : size();
        String atributoOrdenacao = (sort() == null || sort().isBlank()) ? ordenacaoPadrao : sort();

        Sort.Direction direcao = "asc".equalsIgnoreCase(direction())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return PageRequest.of(paginaAtual, tamanhoPagina, Sort.by(direcao, atributoOrdenacao));
    }
}
