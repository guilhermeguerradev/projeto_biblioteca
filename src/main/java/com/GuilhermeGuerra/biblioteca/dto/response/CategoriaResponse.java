package com.GuilhermeGuerra.biblioteca.dto.response;

import com.GuilhermeGuerra.biblioteca.entity.Categoria;
import com.GuilhermeGuerra.biblioteca.entity.Livro;

import java.util.List;

public record CategoriaResponse(
        Long id,
        String nome,
        String descricao,
        List<String> livros
) {

    public static CategoriaResponse fromEntity(Categoria categoria) {
        List<String> livros = categoria.getLivros().stream().map(Livro::getTitulo).toList();

        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao(),
                livros);
    }
}
