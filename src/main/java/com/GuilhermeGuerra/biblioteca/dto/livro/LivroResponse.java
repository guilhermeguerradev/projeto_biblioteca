package com.GuilhermeGuerra.biblioteca.dto.livro;

import com.GuilhermeGuerra.biblioteca.dto.autor.AutorResumidoResponse;
import com.GuilhermeGuerra.biblioteca.dto.categoria.CategoriaResumidaResponse;
import com.GuilhermeGuerra.biblioteca.dto.editora.EditoraResumidaResponse;
import com.GuilhermeGuerra.biblioteca.entity.Livro;

import java.util.List;

public record LivroResponse(
        Long id,
        String titulo,
        String isbn,
        Integer anoPublicacao,
        String edicao,
        String idioma,
        String sinopse,
        Integer numeroPaginas,
        EditoraResumidaResponse editora,
        List<CategoriaResumidaResponse> categorias,
        List<AutorResumidoResponse> autores
) {
    public static LivroResponse fromEntity(Livro livro) {
        List<CategoriaResumidaResponse> categorias = livro.getCategorias().stream()
                .map(
                        c -> CategoriaResumidaResponse.builder()
                                .id(c.getId())
                                .nome(c.getNome())
                                .build()
                ).toList();

        List<AutorResumidoResponse> autores = livro.getAutores().stream()
                .map(
                        a -> AutorResumidoResponse.builder()
                                .id(a.getId())
                                .nome(a.getNome())
                                .build()
                ).toList();

        EditoraResumidaResponse editora = EditoraResumidaResponse
                .builder()
                .id(livro.getEditora().getId())
                .nome(livro.getEditora().getNome())
                .build();

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
