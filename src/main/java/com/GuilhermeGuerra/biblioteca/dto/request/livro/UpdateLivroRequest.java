package com.GuilhermeGuerra.biblioteca.dto.request.livro;


import java.util.List;

public record UpdateLivroRequest(
        String titulo,
        String isbn,
        int anoPublicacao,
        String edicao,
        String idioma,
        String sinopse,
        int numeroPaginas,
        Long editoraId,
        List<Long> categoriasIds,
        List<Long> autoresIds
) {

}
