package com.GuilhermeGuerra.biblioteca.dto.response;

import com.GuilhermeGuerra.biblioteca.entity.Livro;

import java.util.List;

public record AutorResponse(
        Long id,
        String nome,
        String nacionalidade,
        List<Livro> livros
) {
}
