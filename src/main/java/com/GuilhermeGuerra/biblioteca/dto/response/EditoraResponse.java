package com.GuilhermeGuerra.biblioteca.dto.response;

import com.GuilhermeGuerra.biblioteca.entity.Editora;
import com.GuilhermeGuerra.biblioteca.entity.Livro;

import java.util.List;

public record EditoraResponse(
        Long id,
        String nome,
        String cidade,
        String estado,
        String pais,
        List<String> livros
) {
    public static EditoraResponse fromEntity(Editora editora) {
        List<String> livros = editora.getLivros().stream().map(Livro::getTitulo).toList();

        return new EditoraResponse(
                editora.getId(),
                editora.getNome(),
                editora.getCidade(),
                editora.getEstado(),
                editora.getPais(),
                livros
        );
    }
}
