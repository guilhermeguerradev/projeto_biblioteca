package com.GuilhermeGuerra.biblioteca.dto.multa;

import com.GuilhermeGuerra.biblioteca.entity.Multa;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusMulta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MultaResponse(
        Long id,
        Long emprestimoId,
        BigDecimal valor,
        String motivo,
        StatusMulta status,
        LocalDateTime dataPagamento
) {
    public static MultaResponse fromEntity(Multa multa) {
        if (multa == null) {
            return null;
        }
        return new MultaResponse(
                multa.getId(),
                multa.getEmprestimo().getId(),
                multa.getValor(),
                multa.getMotivo(),
                multa.getStatus(),
                multa.getDataPagamento()
        );
    }
}
