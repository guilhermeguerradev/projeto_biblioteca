package com.GuilhermeGuerra.biblioteca.dto.editora;


public record UpdateEditoraRequest(
        String nome,
        String cidade,
        String estado,
        String pais
) {
}
