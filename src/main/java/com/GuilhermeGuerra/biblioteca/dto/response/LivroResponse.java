package com.GuilhermeGuerra.biblioteca.dto.response;

import com.GuilhermeGuerra.biblioteca.entity.Categoria;
import com.GuilhermeGuerra.biblioteca.entity.Livro;

import java.util.List;

public record LivroResponse(
        Long id,
        String titulo,
        String isbn,
        int anoPublicacao,
        String edicao,
        String idioma,
        String sinopse,
        int numeroPaginas,
        EditoraResumidaResponse editora,
        List<CategoriaResumidaResponse> categorias,
        List<AutorResumidoResponse> autores
) {
    public static LivroResponse fromEntity(Livro livro) {
        List<CategoriaResumidaResponse> categorias = livro.getCategorias().stream()
                .map(c -> new CategoriaResumidaResponse(c.getId(), c.getNome())).toList();

        List<AutorResumidoResponse> autores = livro.getAutores().stream()
                .map(a -> new AutorResumidoResponse(a.getId(), a.getNome())).toList();

        EditoraResumidaResponse editora = new EditoraResumidaResponse(livro.getEditora().getId(),livro.getEditora().getNome());

        return new LivroResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getIsbn(),
                livro.getAnoPublicacao(),
                livro.getEdicao(),
                livro.getIdioma(),
                livro.getSinopse(),
                livro.getNumeroPaginas(),
                editora,
                categorias,
                autores
        );

    }
}
