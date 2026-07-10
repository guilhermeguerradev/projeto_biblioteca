package com.GuilhermeGuerra.biblioteca.dto.livro;


import jakarta.validation.constraints.Positive;

import java.util.List;

public record UpdateLivroRequest(
        String titulo,
        String isbn,
        @Positive(message = "O ano de publicação deve ser um número positivo")
        Integer anoPublicacao,
        String edicao,
        String idioma,
        String sinopse,
        @Positive(message = "O número de páginas deve ser maior que zero")
        Integer numeroPaginas,
        Long editoraId,
        List<Long> categoriasIds,
        List<Long> autoresIds
) {

}
