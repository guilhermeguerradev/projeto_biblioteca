package com.GuilhermeGuerra.biblioteca.dto.exemplar;

import com.GuilhermeGuerra.biblioteca.entity.enums.StatusExemplar;

import java.time.LocalDate;

public record UpdateExemplarRequest(
        Long livroId,
        StatusExemplar status,
        String localizacao,
        LocalDate dataAquisicao,
        String observacao
) {
}
