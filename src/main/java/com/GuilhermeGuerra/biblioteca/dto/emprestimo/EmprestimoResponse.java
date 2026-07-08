package com.GuilhermeGuerra.biblioteca.dto.emprestimo;

import com.GuilhermeGuerra.biblioteca.dto.multa.MultaResponse;
import com.GuilhermeGuerra.biblioteca.entity.Emprestimo;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusEmprestimo;

import java.time.LocalDateTime;

public record EmprestimoResponse(
        Long id,
        Long idUsuario,
        String nomeUsuario,
        Long idColaborador,
        String nomeColaborador,
        Long idExemplar,
        LocalDateTime dataEmprestimo,
        LocalDateTime dataPrevista,
        LocalDateTime dataDevolucao,
        Integer quantidadeRenovacoes,
        StatusEmprestimo status,
        MultaResponse multa
) {
    public static EmprestimoResponse fromEntity(Emprestimo emprestimo) {
        return new EmprestimoResponse(
                emprestimo.getId(),
                emprestimo.getUsuario().getId(),
                emprestimo.getUsuario().getNome(),
                emprestimo.getColaborador().getId(),
                emprestimo.getColaborador().getUsuario().getNome(),
                emprestimo.getExemplar().getId(),
                emprestimo.getDataEmprestimo(),
                emprestimo.getDataPrevista(),
                emprestimo.getDataDevolucao(),
                emprestimo.getQuantidadeRenovacoes(),
                emprestimo.getStatus(),
                emprestimo.getMulta() == null ? null : MultaResponse.fromEntity(emprestimo.getMulta())
        );
    }
}
