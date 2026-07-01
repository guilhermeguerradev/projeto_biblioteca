package com.GuilhermeGuerra.biblioteca.dto.request.livro;

import jakarta.validation.constraints.*;

import java.util.List;

public record CreateLivroRequest(
        @NotBlank(message = "O titulo é obrigatório")
        String titulo,

        @NotBlank(message = "ISBN é obrigatório")
        String isbn,

        @Min(value = 100, message = "O ano publicação inválido")
        int anoPublicacao,

        @NotBlank(message = "A edição é obrigatório")
        String edicao,

        @NotBlank(message = "o idioma é obrigatório")
        String idioma,

        @NotBlank(message = "A sinopse é obrigatório")
        String sinopse,

        @Positive(message = "O número de páginas deve ser maior que 0")
        int numeroPaginas,

        @NotNull(message = "O livro deve pertencer a uma editora")
        Long editoraId,

        @NotEmpty(message = "O livro deve ter pelo menos uma categoria")
        List<Long> categoriasIds,

        @NotEmpty(message = "O livro deve ter pelo menos um autor")
        List<Long> autoresIds


) {
}
