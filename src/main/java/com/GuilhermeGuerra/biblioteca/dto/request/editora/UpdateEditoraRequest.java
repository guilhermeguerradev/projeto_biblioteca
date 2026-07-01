package com.GuilhermeGuerra.biblioteca.dto.request.editora;


public record UpdateEditoraRequest(
        String nome,
        String cidade,
        String estado,
        String pais
) {
}
