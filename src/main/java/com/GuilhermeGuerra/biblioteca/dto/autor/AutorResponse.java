package com.GuilhermeGuerra.biblioteca.dto.autor;

import com.GuilhermeGuerra.biblioteca.entity.Autor;
import com.GuilhermeGuerra.biblioteca.entity.Livro;

import java.util.List;

public record AutorResponse(
        Long id,
        String nome,
        String nacionalidade,
        List<String> livros
) {
    public static AutorResponse fromEntity(Autor autor) {
        List<String> titulos = autor.getLivros().stream()
                .map(Livro::getTitulo) // ajusta o nome do campo se for diferente
                .toList();

        return new AutorResponse(
                autor.getId(),
                autor.getNome(),
                autor.getNacionalidade(),
                titulos
        );
    }
}
