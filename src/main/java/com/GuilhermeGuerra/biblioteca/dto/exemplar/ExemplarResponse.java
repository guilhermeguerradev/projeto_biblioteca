package com.GuilhermeGuerra.biblioteca.dto.exemplar;


import com.GuilhermeGuerra.biblioteca.entity.Emprestimo;
import com.GuilhermeGuerra.biblioteca.entity.Exemplar;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusExemplar;

import java.time.LocalDate;
import java.util.List;

public record ExemplarResponse(
        Long id,
        Long livroId,
        String livroTitulo,
        String codigoPatrominio,
        StatusExemplar status,
        String localizacao,
        LocalDate dataAquisicao,
        String observacao,
        List<Long> emprestimosId
) {
    public static ExemplarResponse fromEntity(Exemplar exemplar) {
        List<Long> emprestimosId = exemplar.getEmprestimos().stream().map(Emprestimo::getId).toList();

        return new ExemplarResponse(
                exemplar.getId(),
                exemplar.getLivro().getId(),
                exemplar.getLivro().getTitulo(),
                exemplar.getCodigoPatrimonio(),
                exemplar.getStatus(),
                exemplar.getLocalizacao(),
                exemplar.getDataAquisicao(),
                exemplar.getObservacao(),
                emprestimosId
        );
    }
}
